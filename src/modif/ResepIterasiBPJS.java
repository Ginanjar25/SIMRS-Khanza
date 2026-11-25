package modif;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Pembantu resep iterasi Apotek Online BPJS (obat kronis bulan ke-2 dan ke-3 tanpa kunjungan poli).
 * Resep iterasi dibuat ApotekBPJSPermintaanIterResep di No. Rawat kunjungan bulan ke-1 (SEP induk) dan dicatat di
 * permintaan_resep_iterasi_bpjs (no_resep_awal, no_resep, status_iter "Iterasi Ke 1"/"Iterasi Ke 2").
 * Seluruh obat resep iterasi berada di luar paket INA-CBG sehingga divalidasi sebagai obat kronis (piutang).
 */
public final class ResepIterasiBPJS {
    /** Jumlah hari obat satu kali pengambilan (JHO resep iterasi). */
    public static final int HARI_OBAT=30;
    /** Resep iterasi boleh diambil paling cepat sekian hari sebelum jatuh tempo. */
    public static final int TOLERANSI_HARI=3;

    private ResepIterasiBPJS(){}

    /** status_iter ("Iterasi Ke 1"/"Iterasi Ke 2") bila noresep adalah resep iterasi, selain itu "". */
    public static String statusIter(String noresep){
        return ambil("select permintaan_resep_iterasi_bpjs.status_iter from permintaan_resep_iterasi_bpjs where permintaan_resep_iterasi_bpjs.no_resep=?",noresep);
    }

    public static boolean isIterasi(String noresep){
        return !statusIter(noresep).equals("");
    }

    /**
     * Mengecek apakah resep iterasi sudah boleh diambil hari ini.
     * @return "" bila boleh, selain itu pesan alasan penolakan
     */
    public static String cekBolehAmbil(String noresep){
        String status=statusIter(noresep);
        if(status.equals("")){
            return "";
        }
        String awal=ambil("select permintaan_resep_iterasi_bpjs.no_resep_awal from permintaan_resep_iterasi_bpjs where permintaan_resep_iterasi_bpjs.no_resep=?",noresep);
        // pengambilan sebelumnya: Iterasi Ke 1 -> resep awal, Iterasi Ke 2 -> resep Iterasi Ke 1
        String sebelum=status.equals("Iterasi Ke 2")?
                ambil("select permintaan_resep_iterasi_bpjs.no_resep from permintaan_resep_iterasi_bpjs where permintaan_resep_iterasi_bpjs.no_resep_awal=? and permintaan_resep_iterasi_bpjs.status_iter='Iterasi Ke 1'",awal):awal;
        String tglsebelum=ambil("select if(resep_obat.tgl_perawatan='0000-00-00','',resep_obat.tgl_perawatan) from resep_obat where resep_obat.no_resep=?",sebelum);
        if(tglsebelum.equals("")){
            return "Resep "+status+" belum bisa diambil karena pengambilan sebelumnya ("+
                    (status.equals("Iterasi Ke 2")?"Iterasi Ke 1":"resep awal")+" No. "+sebelum+") belum divalidasi.";
        }
        String[] jadwal=ambil2(
            "select date_format(greatest(resep_obat.tgl_peresepan,date_add(?,interval "+HARI_OBAT+" day)),'%d-%m-%Y'),"+
            "datediff(greatest(resep_obat.tgl_peresepan,date_add(?,interval "+HARI_OBAT+" day)),curdate()) from resep_obat where resep_obat.no_resep=?",
            tglsebelum,tglsebelum,noresep);
        if(jadwal==null){
            return "Data resep iterasi No. "+noresep+" tidak ditemukan.";
        }
        int sisahari=Integer.parseInt(jadwal[1]);
        if(sisahari>TOLERANSI_HARI){
            return "Resep "+status+" belum waktunya diambil.\nJatuh tempo "+jadwal[0]+" (obat sebelumnya diambil "+tglsebelum+
                    "), paling cepat "+TOLERANSI_HARI+" hari sebelumnya.";
        }
        return "";
    }

    /** Keterangan singkat untuk petugas, mis. "Iterasi Ke 1 dari 2x, resep awal 202611190001". */
    public static String info(String noresep){
        String status=statusIter(noresep);
        if(status.equals("")){
            return "";
        }
        String awal=ambil("select permintaan_resep_iterasi_bpjs.no_resep_awal from permintaan_resep_iterasi_bpjs where permintaan_resep_iterasi_bpjs.no_resep=?",noresep);
        String jatah=ambil("select bridging_resep_apotek_bpjs.iterasi from bridging_resep_apotek_bpjs where bridging_resep_apotek_bpjs.no_resep=? limit 1",awal);
        return status+(jatah.equals("")?"":" dari "+jatah.replaceAll("^[0-9]\\. ",""))+", resep awal "+awal;
    }

    private static String ambil(String sql,String... param){
        String[] hasil=ambil2(sql,param);
        return hasil==null||hasil[0]==null?"":hasil[0];
    }

    private static String[] ambil2(String sql,String... param){
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            Connection koneksi=koneksiDB.condb();
            ps=koneksi.prepareStatement(sql);
            for(int i=0;i<param.length;i++){
                ps.setString(i+1,param[i]);
            }
            rs=ps.executeQuery();
            if(rs.next()){
                String[] hasil=new String[rs.getMetaData().getColumnCount()];
                for(int i=0;i<hasil.length;i++){
                    hasil[i]=rs.getString(i+1);
                }
                return hasil;
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Resep Iterasi : "+e);
        } finally {
            try {
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
