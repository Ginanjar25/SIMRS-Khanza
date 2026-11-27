package modif;

import fungsi.akses;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data tiket antrean anjungan dan simpan registrasi tiket Onsite.
 * Simpan memakai koneksi tersendiri, karena koneksiDB.condb() dipakai bersama seluruh aplikasi
 * (termasuk thread antrol) sehingga query lain bisa ikut ter-commit / rollback.
 */
public class RegistrasiAnjungan {
    // nama lock sama dengan aplikasi anjungan, jangan diganti
    private static final String NAMA_LOCK="anjungan_registrasi";
    private static final int TUNGGU_LOCK=10,MAKS_COBA=5,DATA_GANDA=1062;
    // registrasi langsung di Khanza : tanpa nomor FO. Kategori selain A/B dan urut mulai 10001 supaya
    // nomor FO berikutnya di anjungan (urut terbesar per A/B) tidak melompat
    private static final String KATEGORI_LANGSUNG="-";
    private static final int URUT_LANGSUNG=10000;
    private final Connection koneksi=koneksiDB.condb();
    private String pesan="";

    /** Jumlah tiket hari ini yang belum dilayani, -1 bila gagal dibaca. */
    public int jumlahMenunggu(){
        int jumlah=-1;
        try (PreparedStatement ps=koneksi.prepareStatement(
                "select count(*) from side_db.anjungan_antrean_poli where anjungan_antrean_poli.tanggal=curdate() "+
                "and anjungan_antrean_poli.status_fo in ('MENUNGGU','DIPANGGIL')");
             ResultSet rs=ps.executeQuery()) {
            if(rs.next()){
                jumlah=rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return jumlah;
    }

    /**
     * Tiket hari ini, WEBSITE paling atas lalu urut waktu cetak.
     * Kolom side_db utf8mb4 dikonversi ke latin1 supaya index tabel utama tetap terpakai.
     * @param kddokter  kosong untuk semua dokter
     * @param semua     true : termasuk tiket SELESAI dan BATAL
     */
    public List<TiketAnjungan> daftarTiket(String kdpoli,String kddokter,boolean semua) throws SQLException {
        List<TiketAnjungan> hasil=new ArrayList<>();
        try (PreparedStatement ps=koneksi.prepareStatement(
                "select tiket.id,tiket.tanggal,tiket.kd_dokter,ifnull(dokter.nm_dokter,'') as nm_dokter,tiket.kd_poli,ifnull(poliklinik.nm_poli,'') as nm_poli,"+
                "tiket.kategori_huruf,tiket.jalur,tiket.cara_bayar_detail,tiket.no_tiket_fo,tiket.no_reg_reserved,ifnull(tiket.no_rawat,'') as no_rawat,"+
                "tiket.status_fo,ifnull(tiket.meja_fo,'') as meja_fo,time(tiket.waktu_cetak) as jam_cetak,ifnull(reg_periksa.no_rkm_medis,'') as no_rkm_medis,ifnull(reg_periksa.stts,'') as stts "+
                "from side_db.anjungan_antrean_poli as tiket "+
                "left join dokter on dokter.kd_dokter=convert(tiket.kd_dokter using latin1) "+
                "left join poliklinik on poliklinik.kd_poli=convert(tiket.kd_poli using latin1) "+
                "left join reg_periksa on reg_periksa.no_rawat=convert(tiket.no_rawat using latin1) "+
                "where tiket.tanggal=curdate() "+(kdpoli.equals("")?"":"and tiket.kd_poli=? ")+(kddokter.equals("")?"":"and tiket.kd_dokter=? ")+
                (semua?"":"and tiket.status_fo in ('MENUNGGU','DIPANGGIL','DILAYANI') ")+
                "order by if(tiket.jalur='WEBSITE',0,1),tiket.waktu_cetak,tiket.id")) {
            int i=1;
            if(!kdpoli.equals("")){
                ps.setString(i++,kdpoli);
            }
            if(!kddokter.equals("")){
                ps.setString(i,kddokter);
            }
            try (ResultSet rs=ps.executeQuery()) {
                while(rs.next()){
                    TiketAnjungan tiket=new TiketAnjungan();
                    tiket.id=rs.getLong("id");
                    tiket.tanggal=rs.getString("tanggal");
                    tiket.kddokter=rs.getString("kd_dokter");
                    tiket.nmdokter=rs.getString("nm_dokter");
                    tiket.kdpoli=rs.getString("kd_poli");
                    tiket.nmpoli=rs.getString("nm_poli");
                    tiket.kategori=rs.getString("kategori_huruf");
                    tiket.jalur=rs.getString("jalur");
                    tiket.carabayar=rs.getString("cara_bayar_detail");
                    tiket.notiketfo=rs.getString("no_tiket_fo");
                    tiket.noreg=rs.getString("no_reg_reserved");
                    tiket.norawat=rs.getString("no_rawat");
                    tiket.status=rs.getString("status_fo");
                    tiket.jamcetak=rs.getString("jam_cetak");
                    tiket.norm=rs.getString("no_rkm_medis");
                    tiket.meja=rs.getString("meja_fo");
                    tiket.sttsregistrasi=rs.getString("stts");
                    hasil.add(tiket);
                }
            }
        }
        return hasil;
    }

    /** Status terbaru tiket, kosong bila tidak ditemukan. */
    public String statusTiket(long id) throws SQLException {
        try (PreparedStatement ps=koneksi.prepareStatement(
                "select anjungan_antrean_poli.status_fo from side_db.anjungan_antrean_poli where anjungan_antrean_poli.id=? and anjungan_antrean_poli.tanggal=curdate()")) {
            ps.setLong(1,id);
            try (ResultSet rs=ps.executeQuery()) {
                return rs.next()?rs.getString(1):"";
            }
        }
    }

    /** true bila reserve No.Reg tiket Onsite masih ada di antriloketcetak. */
    public boolean isReserveAda(TiketAnjungan tiket) throws SQLException {
        return ambilAngka(koneksi,"select count(*) from antriloketcetak where antriloketcetak.tanggal=? and antriloketcetak.kd_poli=? "+
            "and antriloketcetak.kd_dokter=? and antriloketcetak.nomor=? and antriloketcetak.asal='Baru'",
            tiket.tanggal,tiket.kdpoli,tiket.kddokter,tiket.noreg)>0;
    }

    /** Tiket hari ini milik no_rawat yang belum selesai (id, nomor, kategori, cara bayar), null bila bukan pasien anjungan atau sudah selesai. */
    public TiketAnjungan tiketAktif(String norawat){
        TiketAnjungan tiket=null;
        try (PreparedStatement ps=koneksi.prepareStatement(
                "select anjungan_antrean_poli.id,if(anjungan_antrean_poli.no_tiket_fo='',anjungan_antrean_poli.no_tiket_poli,anjungan_antrean_poli.no_tiket_fo) as nomor,"+
                "anjungan_antrean_poli.kategori_huruf,anjungan_antrean_poli.cara_bayar_detail from side_db.anjungan_antrean_poli "+
                "where anjungan_antrean_poli.no_rawat=? and anjungan_antrean_poli.tanggal=curdate() "+
                "and anjungan_antrean_poli.status_fo in ('MENUNGGU','DIPANGGIL','DILAYANI')")) {
            ps.setString(1,norawat);
            try (ResultSet rs=ps.executeQuery()) {
                if(rs.next()){
                    tiket=new TiketAnjungan();
                    tiket.id=rs.getLong("id");
                    tiket.notiketfo=rs.getString("nomor");
                    tiket.kategori=rs.getString("kategori_huruf");
                    tiket.carabayar=rs.getString("cara_bayar_detail");
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return tiket;
    }

    /**
     * Menandai tiket SELESAI (Task ID 3) setelah barcode dicetak. Tidak lewat API anjungan, karena Selesai di sana
     * menulis ulang antripoli menjadi status 0 dan bisa mencetak label lagi.
     */
    public boolean selesaikan(long id,String petugas){
        try {
            return jalankan(koneksi,"update side_db.anjungan_antrean_poli set anjungan_antrean_poli.status_fo='SELESAI',"+
                "anjungan_antrean_poli.jam_dipanggil=ifnull(anjungan_antrean_poli.jam_dipanggil,now()),anjungan_antrean_poli.jam_selesai=now(),"+
                "anjungan_antrean_poli.petugas_fo=? where anjungan_antrean_poli.id=? and anjungan_antrean_poli.tanggal=curdate() "+
                "and anjungan_antrean_poli.status_fo in ('MENUNGGU','DIPANGGIL','DILAYANI')",petugas,id+"")==1;
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            return false;
        }
    }

    /** Dokter yang praktik hari ini ditambah dokter yang punya tiket hari ini (kode, nama). */
    /** Poli yang praktik hari ini atau punya tiket hari ini (kode → nama). */
    public Map<String,String> daftarPoli(){
        Map<String,String> hasil=new LinkedHashMap<>();
        isiDokter(hasil,
            "select poliklinik.kd_poli,poliklinik.nm_poli from poliklinik where poliklinik.kd_poli in "+
            "(select jadwal.kd_poli from jadwal where jadwal.hari_kerja=elt(dayofweek(curdate()),'AKHAD','SENIN','SELASA','RABU','KAMIS','JUMAT','SABTU')) "+
            "or poliklinik.kd_poli in (select convert(tiket.kd_poli using latin1) from side_db.anjungan_antrean_poli as tiket where tiket.tanggal=curdate()) "+
            "order by poliklinik.nm_poli");
        return hasil;
    }

    /** Dokter yang praktik hari ini atau punya tiket hari ini (kode → nama), kdpoli "" berarti semua poli. */
    public Map<String,String> daftarDokter(String kdpoli){
        Map<String,String> hasil=new LinkedHashMap<>();
        String poli=kdpoli.equals("")?"":"and jadwal.kd_poli=? ";
        isiDokter(hasil,
            "select distinct dokter.kd_dokter,dokter.nm_dokter from jadwal inner join dokter on dokter.kd_dokter=jadwal.kd_dokter "+
            "where jadwal.hari_kerja=elt(dayofweek(curdate()),'AKHAD','SENIN','SELASA','RABU','KAMIS','JUMAT','SABTU') "+poli+"order by dokter.nm_dokter",kdpoli);
        isiDokter(hasil,
            "select distinct dokter.kd_dokter,dokter.nm_dokter from side_db.anjungan_antrean_poli as tiket "+
            "inner join dokter on dokter.kd_dokter=convert(tiket.kd_dokter using latin1) where tiket.tanggal=curdate() "+poli.replace("jadwal.","tiket.")+"order by dokter.nm_dokter",kdpoli);
        return hasil;
    }

    // isi kode → nama dari query, parameter "" diabaikan (query tanpa filter)
    private void isiDokter(Map<String,String> hasil,String sql,String... parameter){
        try (PreparedStatement ps=koneksi.prepareStatement(sql)) {
            int i=1;
            for(String isi:parameter){
                if(!isi.equals("")){
                    ps.setString(i++,isi);
                }
            }
            try (ResultSet rs=ps.executeQuery()) {
                while(rs.next()){
                    hasil.put(rs.getString(1),rs.getString(2));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    /**
     * Mencatat registrasi yang dibuat langsung di Khanza (tanpa tiket anjungan) ke antrean anjungan :
     * nomor FO kosong, langsung DILAYANI. Hanya registrasi hari ini dan bukan IGD.
     * @param jalur ONSITE, atau BOOKING bila dari menu Booking Registrasi
     * @return false bila seharusnya dicatat tetapi gagal
     */
    public boolean catatLangsung(String norawat,String petugas,String jalur){
        try (PreparedStatement ps=koneksi.prepareStatement(
                "select reg_periksa.tgl_registrasi,reg_periksa.kd_poli,reg_periksa.kd_dokter,reg_periksa.no_reg,reg_periksa.kd_pj,penjab.png_jawab,"+
                "ifnull((select maping_poli_bpjs.kd_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_rs=reg_periksa.kd_poli limit 1),'') as poli_bpjs "+
                "from reg_periksa inner join penjab on penjab.kd_pj=reg_periksa.kd_pj "+
                "where reg_periksa.no_rawat=? and reg_periksa.tgl_registrasi=curdate() and reg_periksa.kd_poli<>'IGDK'")) {
            ps.setString(1,norawat);
            try (ResultSet rs=ps.executeQuery()) {
                if(!rs.next()){
                    return true;
                }
                // awalan tiket dan kelompok cara bayar sama dengan aplikasi anjungan
                String kdpoli=rs.getString("kd_poli"),kddokter=rs.getString("kd_dokter"),noreg=rs.getString("no_reg");
                String kode=kdpoli.equals("U0026")?"LAK":rs.getString("poli_bpjs");
                if(kode.equals("")){
                    kode=kdpoli;
                }
                String carabayar="ASURANSI";
                if(rs.getString("kd_pj").equals("BPJ")){
                    carabayar="BPJS";
                }else if(rs.getString("kd_pj").equals("-")||rs.getString("png_jawab").toUpperCase().contains("UMUM")){
                    carabayar="UMUM";
                }
                String nomorpoli=noreg;
                try {
                    nomorpoli=String.format("%03d",Integer.parseInt(noreg.trim()));
                } catch (NumberFormatException e) {
                    System.out.println("Notifikasi : "+e);
                }
                return simpanLangsung(rs.getString("tgl_registrasi"),kdpoli,kddokter,kode,carabayar,noreg,
                    kode+"-"+(carabayar.equals("BPJS")?"B":"A")+nomorpoli,norawat,petugas,jalur);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            return false;
        }
    }

    private boolean simpanLangsung(String tanggal,String kdpoli,String kddokter,String kode,String carabayar,String noreg,String notiketpoli,String norawat,String petugas,String jalur) throws SQLException {
        for(int coba=1;;coba++){
            int urut=ambilAngka(koneksi,"select ifnull(max(anjungan_antrean_poli.urut_fo),"+URUT_LANGSUNG+")+1 from side_db.anjungan_antrean_poli "+
                "where anjungan_antrean_poli.tanggal=? and anjungan_antrean_poli.kd_poli=? and anjungan_antrean_poli.kd_dokter=? and anjungan_antrean_poli.kategori_huruf=?",
                tanggal,kdpoli,kddokter,KATEGORI_LANGSUNG);
            try {
                return jalankan(koneksi,"insert into side_db.anjungan_antrean_poli (tanggal,kd_poli,kd_dokter,kode_poli,kategori_huruf,jalur,cara_bayar_detail,urut_fo,"+
                    "no_tiket_fo,no_reg_reserved,no_tiket_poli,no_rawat,status_fo,jam_dipanggil,petugas_fo,waktu_cetak) "+
                    "values(?,?,?,?,?,?,?,?,'',?,?,?,'DILAYANI',now(),?,now())",
                    tanggal,kdpoli,kddokter,kode,KATEGORI_LANGSUNG,jalur,carabayar,urut+"",noreg,notiketpoli,norawat,petugas)==1;
            } catch (SQLException e) {
                // urut dipakai petugas lain pada saat yang sama, ulangi dengan urut berikutnya
                if((e.getErrorCode()!=DATA_GANDA)||(coba>=MAKS_COBA)){
                    throw e;
                }
            }
        }
    }

    /**
     * Menyimpan registrasi tiket Onsite : insert reg_periksa, menautkan tiket, dan menghapus reserve
     * antriloketcetak dalam satu transaksi. Semua tersimpan atau tidak sama sekali.
     * @param registrasi 19 isian reg_periksa, No.Reg dan No.Rawat diisi di sini
     * @return no_rawat yang tersimpan, kosong bila gagal (alasan di getPesan())
     */
    public String simpan(TiketAnjungan tiket,String[] registrasi,String petugas){
        String norawat="";
        pesan="";
        Connection khusus=null;
        boolean terkunci=false;
        try {
            khusus=SettingAnjungan.koneksiBaru();
            jalankan(khusus,"set session innodb_lock_wait_timeout=10");
            terkunci=ambilAngka(khusus,"select get_lock(?,?)",NAMA_LOCK,TUNGGU_LOCK+"")==1;
            if(!terkunci){
                pesan="Proses registrasi anjungan sedang sibuk, silahkan simpan lagi..!!";
            }else{
                khusus.setAutoCommit(false);
                for(int coba=1;norawat.equals("")&&pesan.equals("");coba++){
                    try {
                        norawat=simpanTransaksi(khusus,tiket,registrasi,petugas);
                    } catch (SQLException e) {
                        khusus.rollback();
                        // no_rawat dipakai DlgReg biasa yang tidak memakai lock, ulangi dengan nomor berikutnya
                        if((e.getErrorCode()!=DATA_GANDA)||(coba>=MAKS_COBA)){
                            throw e;
                        }
                    }
                }
                if(!norawat.equals("")){
                    simpanTrack(khusus,registrasi,norawat,tiket.noreg);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            if(norawat.equals("")){
                pesan="Gagal menyimpan registrasi tiket "+tiket.notiketfo+", cek data registrasi sebelum mengulang..!!\n"+e.getMessage();
            }
        } finally{
            tutup(khusus,terkunci);
        }
        return norawat;
    }

    /** Alasan simpan terakhir gagal. */
    public String getPesan(){
        return pesan;
    }

    private String simpanTransaksi(Connection khusus,TiketAnjungan tiket,String[] registrasi,String petugas) throws SQLException {
        if(ambilAngka(khusus,"select count(*) from reg_periksa where reg_periksa.tgl_registrasi=? and reg_periksa.kd_dokter=? "+
                "and reg_periksa.kd_poli=? and reg_periksa.no_reg=?",tiket.tanggal,tiket.kddokter,tiket.kdpoli,tiket.noreg)>0){
            khusus.rollback();
            pesan="No.Reg "+tiket.noreg+" milik tiket "+tiket.notiketfo+" sudah dipakai registrasi lain..!!";
            return "";
        }

        String awalan=tiket.tanggal.replace("-","/")+"/";
        // no_rawat di tiket ikut dihitung (unik di tabel tiket, registrasinya bisa saja sudah dihapus)
        int nomor=Math.max(
            ambilAngka(khusus,"select ifnull(max(convert(right(reg_periksa.no_rawat,6),signed)),0) from reg_periksa where reg_periksa.no_rawat like ?",awalan+"%"),
            ambilAngka(khusus,"select ifnull(max(convert(right(anjungan_antrean_poli.no_rawat,6),signed)),0) from side_db.anjungan_antrean_poli "+
                "where anjungan_antrean_poli.no_rawat like ?",awalan+"%"))+1;
        String norawat=awalan+String.format("%06d",nomor);

        String[] data=registrasi.clone();
        data[0]=tiket.noreg;
        data[1]=norawat;
        jalankan(khusus,"insert into reg_periksa values("+String.join(",",Collections.nCopies(data.length,"?"))+")",data);

        if(jalankan(khusus,"update side_db.anjungan_antrean_poli set anjungan_antrean_poli.no_rawat=?,anjungan_antrean_poli.status_fo='DILAYANI',"+
                "anjungan_antrean_poli.jam_dipanggil=ifnull(anjungan_antrean_poli.jam_dipanggil,now()),"+
                "anjungan_antrean_poli.petugas_fo=? where anjungan_antrean_poli.id=? and anjungan_antrean_poli.no_rawat is null "+
                "and anjungan_antrean_poli.tanggal=curdate() and anjungan_antrean_poli.status_fo in ('MENUNGGU','DIPANGGIL')",
                norawat,petugas,tiket.id+"")!=1){
            khusus.rollback();
            pesan="Tiket "+tiket.notiketfo+" sudah diproses meja lain..!!";
            return "";
        }

        jalankan(khusus,"delete from antriloketcetak where antriloketcetak.tanggal=curdate() and antriloketcetak.kd_poli=? "+
            "and antriloketcetak.kd_dokter=? and antriloketcetak.nomor=? and antriloketcetak.asal='Baru'",tiket.kdpoli,tiket.kddokter,tiket.noreg);
        khusus.commit();
        return norawat;
    }

    /** Pencatatan trackersql seperti sekuel.menyimpantf2. */
    private void simpanTrack(Connection khusus,String[] registrasi,String norawat,String noreg){
        if(koneksiDB.AKTIFKANTRACKSQL().equals("yes")){
            try {
                String[] data=registrasi.clone();
                data[0]=noreg;
                data[1]=norawat;
                jalankan(khusus,"insert into trackersql values(now(),?,?)",
                    akses.getalamatip()+" insert into reg_periksa values(|"+String.join("|",data)+")",akses.getkode());
                khusus.commit();
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
        }
    }

    private int jalankan(Connection khusus,String sql,String... parameter) throws SQLException {
        try (PreparedStatement ps=khusus.prepareStatement(sql)) {
            for(int i=0;i<parameter.length;i++){
                ps.setString(i+1,parameter[i]);
            }
            return ps.executeUpdate();
        }
    }

    private int ambilAngka(Connection khusus,String sql,String... parameter) throws SQLException {
        try (PreparedStatement ps=khusus.prepareStatement(sql)) {
            for(int i=0;i<parameter.length;i++){
                ps.setString(i+1,parameter[i]);
            }
            try (ResultSet rs=ps.executeQuery()) {
                return rs.next()?rs.getInt(1):0;
            }
        }
    }

    private void tutup(Connection khusus,boolean terkunci){
        if(khusus!=null){
            try {
                // transaksi yang belum selesai dibatalkan sebelum lock dilepas
                if(!khusus.getAutoCommit()){
                    khusus.rollback();
                    khusus.setAutoCommit(true);
                }
                if(terkunci){
                    ambilAngka(khusus,"select release_lock(?)",NAMA_LOCK);
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
            try {
                // menutup koneksi juga melepas lock bila perintah di atas gagal
                khusus.close();
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
        }
    }
}
