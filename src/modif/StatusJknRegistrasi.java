package modif;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Melengkapi kolom Asal (WEB/JKN/ONSITE) dan Status JKN pada tabel registrasi DlgReg.
 * Dulu kedua kolom diambil lewat LEFT JOIN referensi_mobilejkn_bpjs, side_db.reg_periksa_website
 * dan referensi_mobilejkn_bpjs_batal di query utama. Join tersebut lambat karena kolom no_rawat
 * tidak ber-index (1 hari 10 detik, 10 hari 1 menit).
 * Sekarang data diambil terpisah hanya untuk no_rawat yang tampil, lalu digabung dengan aturan
 * yang sama persis, termasuk baris ganda bila satu no_rawat punya lebih dari satu booking (perilaku LEFT JOIN).
 */
public class StatusJknRegistrasi {
    private static final int UKURAN_BATCH=1000;
    private final Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;

    /** Satu baris referensi_mobilejkn_bpjs. */
    private static class BookingJkn {
        private final String nobooking,status;

        private BookingJkn(String nobooking,String status){
            this.nobooking=nobooking;
            this.status=status;
        }
    }

    /**
     * Mengisi kolom asal dan status JKN pada setiap baris.
     * @param daftarbaris    baris hasil query utama, kolom asal dan status JKN masih kosong
     * @param kolomnorawat   indeks kolom no_rawat
     * @param kolomstts      indeks kolom reg_periksa.stts
     * @param kolomasal      indeks kolom asal yang diisi
     * @param kolomstatusjkn indeks kolom status JKN yang diisi
     * @return baris yang sudah lengkap, bisa lebih banyak dari input bila ada booking ganda;
     *         kosong jika gagal membaca data (sama seperti query lama yang gagal)
     */
    public List<Object[]> lengkapi(List<Object[]> daftarbaris,int kolomnorawat,int kolomstts,int kolomasal,int kolomstatusjkn){
        List<Object[]> hasil=new ArrayList<>();
        try {
            Set<String> daftarnorawat=new LinkedHashSet<>();
            for(Object[] baris:daftarbaris){
                daftarnorawat.add(baris[kolomnorawat].toString());
            }

            Map<String,List<BookingJkn>> bookingjkn=ambilBookingJkn(daftarnorawat);
            Map<String,List<String>> registrasiweb=ambilRegistrasiWeb(daftarnorawat);
            Set<String> ganticarabayar=ambilBookingGantiCaraBayar(bookingjkn);

            for(Object[] baris:daftarbaris){
                String norawat=kunci(baris[kolomnorawat].toString());
                String stts=baris[kolomstts]==null?null:baris[kolomstts].toString();

                List<BookingJkn> daftarbooking=bookingjkn.get(norawat);
                if(daftarbooking==null){
                    daftarbooking=Collections.singletonList(null);
                }
                List<String> daftarstatusweb=registrasiweb.get(norawat);
                boolean adaregistrasiweb=daftarstatusweb!=null;
                if(!adaregistrasiweb){
                    daftarstatusweb=Collections.singletonList(null);
                }

                // satu baris per kombinasi booking x registrasi web, sama seperti LEFT JOIN
                for(BookingJkn booking:daftarbooking){
                    for(String statusweb:daftarstatusweb){
                        Object[] barisbaru=baris.clone();
                        barisbaru[kolomasal]=hitungAsal(booking,adaregistrasiweb,ganticarabayar);
                        barisbaru[kolomstatusjkn]=hitungStatusJkn(booking,statusweb,stts);
                        hasil.add(barisbaru);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            hasil.clear();
        }
        return hasil;
    }

    /**
     * Query lama :
     * CASE CONCAT((ISNULL(rmb.nobooking) OR rmbb.nobooking IS NOT NULL), ISNULL(regw.no_rawat))
     * WHEN '10' THEN 'WEB' WHEN '01' THEN 'JKN' WHEN '11' THEN 'ONSITE' ELSE 'Unknown' END
     */
    private String hitungAsal(BookingJkn booking,boolean adaregistrasiweb,Set<String> ganticarabayar){
        boolean tanpabookingjkn=(booking==null)||ganticarabayar.contains(kunci(booking.nobooking));
        if(tanpabookingjkn&&adaregistrasiweb){
            return "WEB";
        }else if(!tanpabookingjkn&&!adaregistrasiweb){
            return "JKN";
        }else if(tanpabookingjkn&&!adaregistrasiweb){
            return "ONSITE";
        }else{
            return "Unknown";
        }
    }

    /**
     * Query lama :
     * IF(ISNULL(rmb.status),IF(ISNULL(regw.status),IF(reg_periksa.stts != 'Batal','-','Batal'),regw.status),rmb.status)
     */
    private String hitungStatusJkn(BookingJkn booking,String statusweb,String stts){
        if((booking!=null)&&(booking.status!=null)){
            return booking.status;
        }else if(statusweb!=null){
            return statusweb;
        }else if((stts!=null)&&!kunci(stts).equals("BATAL")){
            return "-";
        }else{
            // stts NULL di SQL : NULL != 'Batal' bernilai NULL, sehingga hasilnya 'Batal'
            return "Batal";
        }
    }

    /** referensi_mobilejkn_bpjs per no_rawat. */
    private Map<String,List<BookingJkn>> ambilBookingJkn(Set<String> daftarnorawat) throws Exception {
        Map<String,List<BookingJkn>> hasil=new HashMap<>();
        for(List<String> batch:bagiBatch(daftarnorawat)){
            try {
                ps=koneksi.prepareStatement(
                    "select referensi_mobilejkn_bpjs.no_rawat,referensi_mobilejkn_bpjs.nobooking,referensi_mobilejkn_bpjs.status "+
                    "from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.no_rawat in ("+placeholder(batch.size())+")");
                isiParameter(batch);
                rs=ps.executeQuery();
                while(rs.next()){
                    String norawat=kunci(rs.getString("no_rawat"));
                    if(!hasil.containsKey(norawat)){
                        hasil.put(norawat,new ArrayList<BookingJkn>());
                    }
                    hasil.get(norawat).add(new BookingJkn(rs.getString("nobooking"),rs.getString("status")));
                }
            } finally{
                tutup();
            }
        }
        return hasil;
    }

    /** status side_db.reg_periksa_website per no_rawat. */
    private Map<String,List<String>> ambilRegistrasiWeb(Set<String> daftarnorawat) throws Exception {
        Map<String,List<String>> hasil=new HashMap<>();
        for(List<String> batch:bagiBatch(daftarnorawat)){
            try {
                ps=koneksi.prepareStatement(
                    "select reg_periksa_website.no_rawat,reg_periksa_website.status "+
                    "from side_db.reg_periksa_website where reg_periksa_website.no_rawat in ("+placeholder(batch.size())+")");
                isiParameter(batch);
                rs=ps.executeQuery();
                while(rs.next()){
                    String norawat=kunci(rs.getString("no_rawat"));
                    if(!hasil.containsKey(norawat)){
                        hasil.put(norawat,new ArrayList<String>());
                    }
                    hasil.get(norawat).add(rs.getString("status"));
                }
            } finally{
                tutup();
            }
        }
        return hasil;
    }

    /** nobooking yang dibatalkan karena ganti cara bayar (referensi_mobilejkn_bpjs_batal). */
    private Set<String> ambilBookingGantiCaraBayar(Map<String,List<BookingJkn>> bookingjkn) throws Exception {
        Set<String> daftarnobooking=new LinkedHashSet<>();
        for(List<BookingJkn> daftar:bookingjkn.values()){
            for(BookingJkn booking:daftar){
                daftarnobooking.add(booking.nobooking);
            }
        }

        Set<String> hasil=new HashSet<>();
        for(List<String> batch:bagiBatch(daftarnobooking)){
            try {
                ps=koneksi.prepareStatement(
                    "select referensi_mobilejkn_bpjs_batal.nobooking from referensi_mobilejkn_bpjs_batal "+
                    "where referensi_mobilejkn_bpjs_batal.nobooking in ("+placeholder(batch.size())+") "+
                    "and referensi_mobilejkn_bpjs_batal.keterangan like 'Ganti cara bayar%'");
                isiParameter(batch);
                rs=ps.executeQuery();
                while(rs.next()){
                    hasil.add(kunci(rs.getString("nobooking")));
                }
            } finally{
                tutup();
            }
        }
        return hasil;
    }

    /** Memecah daftar menjadi beberapa batch agar isi in (...) tidak terlalu panjang. */
    private List<List<String>> bagiBatch(Set<String> daftar){
        List<String> semua=new ArrayList<>(daftar);
        List<List<String>> hasil=new ArrayList<>();
        for(int awal=0;awal<semua.size();awal+=UKURAN_BATCH){
            hasil.add(semua.subList(awal,Math.min(semua.size(),awal+UKURAN_BATCH)));
        }
        return hasil;
    }

    /** "?,?,?" sebanyak jumlah. */
    private String placeholder(int jumlah){
        return String.join(",",Collections.nCopies(jumlah,"?"));
    }

    private void isiParameter(List<String> batch) throws Exception {
        for(int i=0;i<batch.size();i++){
            ps.setString(i+1,batch.get(i));
        }
    }

    private void tutup() throws Exception {
        if(rs!=null){
            rs.close();
            rs=null;
        }
        if(ps!=null){
            ps.close();
            ps=null;
        }
    }

    /**
     * Kunci pencocokan yang meniru perbandingan teks MySQL/MariaDB (collation _ci) :
     * huruf besar/kecil dianggap sama dan spasi di akhir diabaikan,
     * supaya hasil penggabungan di Java sama dengan hasil JOIN di database.
     */
    private String kunci(String nilai){
        if(nilai==null){
            return null;
        }
        int akhir=nilai.length();
        while((akhir>0)&&(nilai.charAt(akhir-1)==' ')){
            akhir--;
        }
        return nilai.substring(0,akhir).toUpperCase();
    }
}
