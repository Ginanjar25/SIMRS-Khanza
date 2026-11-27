package modif;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * API pemanggil FO aplikasi anjungan (Go). Status tiket, suara, dan Display FO diurus aplikasi anjungan.
 * Setting di database.xml (teks biasa) : URLAPIANJUNGAN = alamat aplikasi anjungan.
 * Nomor loket PC ini diambil dari side_db.set_ip_antrean (ip_address PC, ruang_poli berupa angka), petugas = akses.getkode().
 */
public class ApiAnjungan {
    /** Permintaan ditolak karena status tiket sudah berubah (HTTP 409). */
    public static class StatusBerubah extends Exception {
        public StatusBerubah(String pesan){
            super(pesan);
        }
    }

    private static final int TUNGGU_KONEKSI=3000,TUNGGU_JAWABAN=8000,TUNGGU_CETAK=30000;
    private final ObjectMapper mapper=new ObjectMapper();
    private String url="",meja="",ippc="";

    public ApiAnjungan(){
        try (FileInputStream file=new FileInputStream("setting/database.xml")) {
            Properties prop=new Properties();
            prop.loadFromXML(file);
            url=prop.getProperty("URLAPIANJUNGAN","").trim();
            while(url.endsWith("/")){
                url=url.substring(0,url.length()-1);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    /** Memanggil tiket MENUNGGU berikutnya milik satu dokter, WEBSITE didahulukan. Aman bila dua meja menekan bersamaan. */
    public JsonNode panggilBerikutnya(String kdpoli,String kddokter) throws Exception {
        Map<String,Object> isi=isiDasar();
        isi.put("kd_poli",kdpoli);
        isi.put("kd_dokter",kddokter);
        isi.put("dahulukan_web",true);
        return kirim("/api/pemanggil/fo/dokter/berikutnya",isi).path("panggilan");
    }

    /** Memanggil atau memanggil ulang satu tiket. */
    public JsonNode panggil(long id) throws Exception {
        Map<String,Object> isi=isiDasar();
        isi.put("id",id);
        return kirim("/api/pemanggil/fo/dokter/panggil",isi).path("panggilan");
    }

    /** Menandai tiket MENUNGGU / DIPANGGIL mulai dilayani (DILAYANI), jam_dipanggil diisi aplikasi anjungan bila kosong. */
    public void layani(long id) throws Exception {
        Map<String,Object> isi=isiDasar();
        isi.put("id",id);
        isi.put("status","DILAYANI");
        kirim("/api/pemanggil/fo/dokter/status",isi);
    }

    /** Membatalkan tiket Onsite yang belum terdaftar, aplikasi anjungan ikut menghapus reserve No.Reg. */
    public void batal(TiketAnjungan tiket) throws Exception {
        Map<String,Object> isi=isiDasar();
        isi.put("id",tiket.id);
        isi.put("no_tiket_fo",tiket.notiketfo);
        isi.put("status","BATAL");
        kirim("/api/pemanggil/fo/dokter/status",isi);
    }

    /** Dokter praktik hari ini yang bisa menjadi tujuan pindah (kode, nama, kd_poli, poli, mulai, selesai, penuh). */
    public JsonNode dokterTujuan() throws Exception {
        return kirim("/api/pemanggil/fo/dokter/tujuan",null,TUNGGU_JAWABAN).path("dokter");
    }

    /**
     * Memindah dokter / cara bayar tiket Onsite yang belum terdaftar. Aplikasi anjungan membatalkan tiket lama,
     * menerbitkan tiket baru (langsung DIPANGGIL di meja ini), dan mencetak 2 lembar baru di printer-nya.
     * @return onsite (tiket baru), tercetak, err_printer
     */
    public JsonNode pindah(TiketAnjungan tiket,String kdpoli,String kddokter,String carabayar) throws Exception {
        Map<String,Object> isi=isiDasar();
        isi.put("id",tiket.id);
        isi.put("no_tiket_fo",tiket.notiketfo);
        isi.put("kd_poli",kdpoli);
        isi.put("kd_dokter",kddokter);
        isi.put("cara_bayar",carabayar);
        return kirim("/api/pemanggil/fo/dokter/pindah",isi,TUNGGU_CETAK);
    }

    /** Nomor loket PC ini menurut side_db.set_ip_antrean, dicari sekali per objek. */
    public String loket() throws Exception {
        if(meja.equals("")){
            List<String> daftarip=ipPC();
            for(String ip:daftarip){
                try (PreparedStatement ps=koneksiDB.condb().prepareStatement(
                        "select trim(set_ip_antrean.ruang_poli) from side_db.set_ip_antrean where set_ip_antrean.ip_address=?")) {
                    ps.setString(1,ip);
                    try (ResultSet rs=ps.executeQuery()) {
                        while(rs.next()&&meja.equals("")){
                            // set_ip_antrean juga berisi ruang lain (igd, usg), hanya angka yang berarti loket FO
                            if(rs.getString(1).matches("[0-9]+")&&(Integer.parseInt(rs.getString(1))>0)){
                                meja=String.valueOf(Integer.parseInt(rs.getString(1)));
                                ippc=ip;
                            }
                        }
                    }
                }
                if(!meja.equals("")){
                    break;
                }
            }
            if(meja.equals("")){
                throw new Exception("IP PC ini ("+String.join(", ",daftarip)+") belum terdaftar sebagai loket FO di side_db.set_ip_antrean..!!");
            }
        }
        return meja;
    }

    /** IP PC yang dicocokkan dengan set_ip_antrean, terisi setelah loket() berhasil. */
    public String ipPCTerdaftar(){
        return ippc;
    }

    // IP yang dipakai menghubungi aplikasi anjungan didahulukan (sama dengan yang dilihat aplikasi anjungan), lalu IPv4 PC lainnya
    private List<String> ipPC(){
        List<String> hasil=new ArrayList<>();
        try {
            URL alamat=new URL(url);
            try (Socket soket=new Socket()) {
                soket.connect(new InetSocketAddress(alamat.getHost(),alamat.getPort()>0?alamat.getPort():alamat.getDefaultPort()),TUNGGU_KONEKSI);
                if(soket.getLocalAddress() instanceof Inet4Address){
                    hasil.add(soket.getLocalAddress().getHostAddress());
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        try {
            for(NetworkInterface kartu:Collections.list(NetworkInterface.getNetworkInterfaces())){
                if(kartu.isUp()){
                    for(InetAddress ip:Collections.list(kartu.getInetAddresses())){
                        if((ip instanceof Inet4Address)&&!ip.isLoopbackAddress()&&!ip.isLinkLocalAddress()&&!hasil.contains(ip.getHostAddress())){
                            hasil.add(ip.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return hasil;
    }

    // petugas_fo diisi kode user Khanza di semua aksi (panggil, berikutnya, status, pindah)
    private Map<String,Object> isiDasar() throws Exception {
        Map<String,Object> isi=new LinkedHashMap<>();
        isi.put("loket",loket());
        isi.put("petugas",akses.getkode());
        return isi;
    }

    /** Mengirim permintaan, hasilnya jawaban JSON. Pesan exception siap ditampilkan ke petugas. */
    private JsonNode kirim(String path,Map<String,Object> isi) throws Exception {
        return kirim(path,isi,TUNGGU_JAWABAN);
    }

    // isi null berarti GET, selain itu POST
    private JsonNode kirim(String path,Map<String,Object> isi,int tungguJawaban) throws Exception {
        if(url.equals("")){
            throw new Exception("URLAPIANJUNGAN belum diisi di setting/database.xml..!!");
        }
        HttpURLConnection koneksi=(HttpURLConnection)new URL(url+path).openConnection();
        try {
            koneksi.setConnectTimeout(TUNGGU_KONEKSI);
            koneksi.setReadTimeout(tungguJawaban);
            if(isi!=null){
                byte[] data=mapper.writeValueAsBytes(isi);
                koneksi.setRequestMethod("POST");
                koneksi.setDoOutput(true);
                koneksi.setRequestProperty("Content-Type","application/json");
                // panjang tetap supaya Java tidak mengirim ulang POST otomatis (pasien bisa terpanggil dua kali)
                koneksi.setFixedLengthStreamingMode(data.length);
                try (OutputStream keluar=koneksi.getOutputStream()) {
                    keluar.write(data);
                }
            }
            int kode=koneksi.getResponseCode();
            String jenis=koneksi.getContentType();
            if((jenis==null)||!jenis.contains("json")){
                throw new Exception("Aplikasi anjungan belum mendukung permintaan ini (kode "+kode+"), perbarui aplikasi anjungan..!!");
            }
            JsonNode jawaban=baca(kode<400?koneksi.getInputStream():koneksi.getErrorStream());
            if(kode!=200){
                String pesan=jawaban.path("error").asText();
                if(pesan.equals("Nomor loket tidak valid.")){
                    pesan=pesan+"\nLoket "+meja+" (IP "+ippc+" di set_ip_antrean) melebihi jumlah loket di aplikasi anjungan.";
                }
                if(kode==HttpURLConnection.HTTP_CONFLICT){
                    throw new StatusBerubah(pesan);
                }
                throw new Exception(pesan.equals("")?"Aplikasi anjungan menjawab kode "+kode+"..!!":pesan);
            }
            return jawaban;
        } catch (ConnectException e) {
            throw new Exception("Aplikasi anjungan tidak bisa dihubungi ("+url+")..!!");
        } catch (SocketTimeoutException e) {
            throw new Exception("Aplikasi anjungan tidak menjawab, cek status tiket sebelum mengulang..!!");
        } finally{
            koneksi.disconnect();
        }
    }

    private JsonNode baca(InputStream masuk) throws Exception {
        if(masuk==null){
            return mapper.createObjectNode();
        }
        try (InputStream aliran=masuk) {
            ByteArrayOutputStream isi=new ByteArrayOutputStream();
            byte[] potong=new byte[4096];
            int jumlah;
            while((jumlah=aliran.read(potong))>0){
                isi.write(potong,0,jumlah);
            }
            return mapper.readTree(isi.toString("UTF-8"));
        }
    }
}
