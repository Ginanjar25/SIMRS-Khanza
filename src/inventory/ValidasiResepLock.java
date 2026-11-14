package inventory;

import fungsi.akses;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Kunci validasi resep berbasis tabel validasi_resep_lock (no_resep sebagai PRIMARY KEY).
 * Pengambilan kunci dilakukan dengan satu INSERT sehingga atomik di level database:
 * jika dua user mengambil kunci resep yang sama bersamaan, hanya satu yang berhasil.
 * Kunci yang tidak diperbarui lebih dari BATAS_MENIT dianggap basi (aplikasi crash/mati).
 */
public final class ValidasiResepLock {
    private static final int BATAS_MENIT=30;
    private static final int HEARTBEAT_MS=5*60*1000;
    private static final int ER_DUP_ENTRY=1062;
    private static final Connection koneksi=koneksiDB.condb();

    private final String noResep;
    private final String token;
    private final Timer heartbeat;
    private boolean dilepas=false;

    private ValidasiResepLock(String noResep,String token){
        this.noResep=noResep;
        this.token=token;
        heartbeat=new Timer(HEARTBEAT_MS,e -> perpanjang());
        heartbeat.start();
    }

    /** Mengambil kunci, mengembalikan null jika resep sedang divalidasi user lain (pesan sudah ditampilkan). */
    public static ValidasiResepLock ambil(String noResep,String noRawat){
        PreparedStatement ps=null;
        try {
            ps=koneksi.prepareStatement("delete from validasi_resep_lock where no_resep=? and waktu < (now() - interval "+BATAS_MENIT+" minute)");
            ps.setString(1,noResep);
            ps.executeUpdate();
            ps.close();

            String token=UUID.randomUUID().toString();
            ps=koneksi.prepareStatement("insert into validasi_resep_lock(no_resep,no_rawat,kd_user,token,waktu) values(?,?,?,?,now())");
            ps.setString(1,noResep);
            ps.setString(2,noRawat);
            ps.setString(3,akses.getkode());
            ps.setString(4,token);
            ps.executeUpdate();
            return new ValidasiResepLock(noResep,token);
        } catch (SQLException e) {
            if(e.getErrorCode()==ER_DUP_ENTRY){
                JOptionPane.showMessageDialog(null,"Maaf, Resep sedang dalam proses Validasi"+keteranganPemegang(noResep)+"!!");
            }else{
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Gagal mengunci resep untuk validasi : "+e.getMessage());
            }
            return null;
        } finally {
            tutup(ps);
        }
    }

    /** Dipakai form lain (hapus/edit resep) yang hanya perlu tahu apakah resep sedang divalidasi. */
    public static boolean sedangDikunci(String noResep){
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps=koneksi.prepareStatement("select count(*) from validasi_resep_lock where no_resep=? and waktu >= (now() - interval "+BATAS_MENIT+" minute)");
            ps.setString(1,noResep);
            rs=ps.executeQuery();
            return rs.next()&&rs.getInt(1)>0;
        } catch (SQLException e) {
            System.out.println("Notifikasi : "+e);
            return false;
        } finally {
            tutup(rs);
            tutup(ps);
        }
    }

    /** Membuang kunci basi; dengan noResep terisi, kunci resep tersebut dihapus paksa. */
    public static int reset(String noResep){
        PreparedStatement ps=null;
        try {
            if(noResep==null||noResep.equals("")){
                ps=koneksi.prepareStatement("delete from validasi_resep_lock where waktu < (now() - interval "+BATAS_MENIT+" minute)");
            }else{
                ps=koneksi.prepareStatement("delete from validasi_resep_lock where no_resep=?");
                ps.setString(1,noResep);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Notifikasi : "+e);
            return 0;
        } finally {
            tutup(ps);
        }
    }

    public static String keteranganPemegang(String noResep){
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps=koneksi.prepareStatement("select kd_user,date_format(waktu,'%H:%i') from validasi_resep_lock where no_resep=?");
            ps.setString(1,noResep);
            rs=ps.executeQuery();
            if(rs.next()){
                return " oleh "+rs.getString(1)+" (aktif "+rs.getString(2)+")";
            }
        } catch (SQLException e) {
            System.out.println("Notifikasi : "+e);
        } finally {
            tutup(rs);
            tutup(ps);
        }
        return "";
    }

    public String getNoResep(){
        return noResep;
    }

    public void perpanjang(){
        if(dilepas){
            return;
        }
        PreparedStatement ps=null;
        try {
            ps=koneksi.prepareStatement("update validasi_resep_lock set waktu=now() where no_resep=? and token=?");
            ps.setString(1,noResep);
            ps.setString(2,token);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Notifikasi : "+e);
        } finally {
            tutup(ps);
        }
    }

    /** Hanya menghapus kunci milik sendiri (berdasarkan token), aman dipanggil berulang. */
    public void lepas(){
        if(dilepas){
            return;
        }
        dilepas=true;
        heartbeat.stop();
        PreparedStatement ps=null;
        try {
            ps=koneksi.prepareStatement("delete from validasi_resep_lock where no_resep=? and token=?");
            ps.setString(1,noResep);
            ps.setString(2,token);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Notifikasi : "+e);
        } finally {
            tutup(ps);
        }
    }

    private static void tutup(AutoCloseable c){
        if(c!=null){
            try {
                c.close();
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
        }
    }
}
