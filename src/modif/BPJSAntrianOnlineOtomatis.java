package modif;

import bridging.ApiBPJS;
import bridging.ApiMobileJKN;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.FileInputStream;
import java.io.InterruptedIOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Kirim antrean Mobile JKN (antrean/add) otomatis setelah registrasi, tanpa dialog.
 * Proses bisnis sama dengan dialog modif.BPJSAntrianOnline: data pasien dari setNoRm2/isRawat,
 * pemilihan rujukan/SKDP dan tujuan kunjungan dari formWindowOpened, validasi dari tombol Simpan,
 * dan pengiriman dari SimpanAntrianOnSite. Semua proses berjalan di background agar form tidak freeze.
 * Bila gagal, petugas diberi pesan berisi No.Rawat dan nama pasien, lalu bisa kirim ulang manual
 * lewat tombol "Kirim Antrol BPJS" di Registrasi (dialog lama).
 */
public final class BPJSAntrianOnlineOtomatis {
    private static final String[] TUJUAN_KUNJUNGAN={"Rujukan FKTP","Internal","Kontrol","Post Ranap","Rujukan Antar RS"};
    private static final String[] JENIS_KUNJUNGAN={"1","2","3","3","4"};
    /** Batas waktu tunggu (milidetik) koneksi ke server BPJS dan tunggu jawaban BPJS. */
    private static final int WAKTU_TUNGGU_KONEKSI=10000,WAKTU_TUNGGU_RESPON=20000;
    private static final ExecutorService executor=Executors.newCachedThreadPool(r -> {
        Thread t=new Thread(r,"AntrolBPJSOtomatis");
        t.setDaemon(true);
        return t;
    });

    private final Connection koneksi=koneksiDB.condb();
    private final sekuel Sequel=new sekuel();
    private final ObjectMapper mapper=new ObjectMapper();

    // data registrasi dari DlgReg
    private final String noRawat,kodepolireg,kodedokterreg,nomorreg;
    // setting, dibaca di EDT saat kirim() karena getter koneksiDB tidak thread-safe
    private String link="",consIdVclaim="",userKeyVclaim="",urlMobileJKN="",consIdMobileJKN="",userKeyMobileJKN="",ADDANTRIANAPIMOBILEJKN="no";
    private int interval=360;
    private final int day=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    private final String tanggalSEP=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    // data pasien (setara isi form dialog)
    private String noRM="",nmPasien="",noKartu="",noTelp="",nik="",kdPoli="",nmPoli="",kdDPJP="",nmDPJP="",
            noRujukan="",noSKDP="",no_peserta="";
    private int tujuanKunjungan=0;
    // gangguan BPJS (timeout / tidak bisa dihubungi) dan kondisi antrean tidak terkirim
    private String gangguanBpjs="";
    private boolean tanpaReferensi=false;

    private BPJSAntrianOnlineOtomatis(String noRawat,String kdpoli,String kddokter,String noreg){
        this.noRawat=noRawat;
        this.kodepolireg=kdpoli;
        this.kodedokterreg=kddokter;
        this.nomorreg=noreg;
    }

    /**
     * Mulai kirim antrean di background. Dipanggil dari EDT; parameter sama dengan setNoRm2 di dialog.
     * @param norwt no_rawat
     * @param kdpoli kode poli RS
     * @param kddokter kode dokter RS
     * @param noreg nomor registrasi (nomor antrean)
     */
    public static void kirim(String norwt,String kdpoli,String kddokter,String noreg){
        final BPJSAntrianOnlineOtomatis antrol=new BPJSAntrianOnlineOtomatis(norwt,kdpoli,kddokter,noreg);
        antrol.bacaSetting();
        try {
            executor.execute(antrol::proses);
        } catch (RejectedExecutionException e) {
            System.out.println("Notif Antrol Otomatis : "+e);
        }
    }

    private void bacaSetting(){
        try {
            link=koneksiDB.URLAPIBPJS();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
        try {
            ADDANTRIANAPIMOBILEJKN=koneksiDB.ADDANTRIANAPIMOBILEJKN();
        } catch (Exception e) {
            ADDANTRIANAPIMOBILEJKN="";
            System.out.println("Notif : "+e);
        }
        try {
            consIdVclaim=koneksiDB.CONSIDAPIBPJS();
            userKeyVclaim=koneksiDB.USERKEYAPIBPJS();
            urlMobileJKN=koneksiDB.URLAPIMOBILEJKN();
            consIdMobileJKN=koneksiDB.CONSIDAPIMOBILEJKN();
            userKeyMobileJKN=koneksiDB.USERKEYAPIMOBILEJKN();
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
        try {
            Properties prop=new Properties();
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            interval=Integer.parseInt(prop.getProperty("INTERVALESTIMASIDEFAULT"));
        } catch (Exception e) {
            interval=360;
        }
    }

    /** Alur lengkap di background: isi data, cari rujukan, validasi, kirim. */
    private void proses(){
        String pesan=null;
        try {
            isiData();
            cariRujukan();
            pesan=validasiDanSimpan();
            if(pesan!=null&&!gangguanBpjs.equals("")){
                pesan="Server BPJS "+gangguanBpjs+".\nAntrean belum tercatat terkirim.";
            }
        } catch (Throwable e) {
            System.out.println("Notif Antrol Otomatis : "+e);
            pesan="Maaf, antrian mobile JKN gagal dibuat. Silahkan cek jadwal dokter / Nomor Referensi..!!";
        }
        if(pesan!=null){
            final String isi="Antrol BPJS No.Rawat "+noRawat+(nmPasien.equals("")?"":" ("+nmPasien+")")+" :\n"+pesan+
                    "\n\nSilahkan kirim ulang lewat tombol \"Kirim Antrol BPJS\".";
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,isi));
        }
    }

    /** Setara setNoRm2 + isRawat di dialog. */
    private void isiData(){
        kdPoli=Sequel.cariIsi("select maping_poli_bpjs.kd_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_rs=?",kodepolireg);
        nmPoli=Sequel.cariIsi("select maping_poli_bpjs.nm_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs=?",kdPoli);
        kdDPJP=Sequel.cariIsi("select maping_dokter_dpjpvclaim.kd_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter=?",kodedokterreg);
        nmDPJP=Sequel.cariIsi("select maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter=?",kodedokterreg);

        noRM=Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ",noRawat);
        nmPasien=Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ",noRM);
        noKartu=Sequel.cariIsi("select pasien.no_peserta from pasien where pasien.no_rkm_medis=? ",noRM);
        noTelp=Sequel.cariIsi("select left(pasien.no_tlp, 13) as no_tlp from pasien where pasien.no_rkm_medis=? ",noRM);
        nik=Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis=? ",noRM);
    }

    /** Setara formWindowOpened di dialog: tentukan No.Rujukan, No.SKDP, dan tujuan kunjungan. */
    private void cariRujukan(){
        ApiBPJS api=new ApiBPJS();
        try {
            String URL=link+"/Rujukan/Peserta/"+noKartu;
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID",consIdVclaim);
            String utc=String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp",utc);
            headers.add("X-Signature",api.getHmac(utc));
            headers.add("user_key",userKeyVclaim);
            HttpEntity requestEntity=new HttpEntity(headers);
            JsonNode root=mapper.readTree(pasangWaktuTunggu(api.getRest()).exchange(URL,HttpMethod.GET,requestEntity,String.class).getBody());
            JsonNode nameNode=root.path("metaData");

            String skdp_rajal="",skdp_ranap="",rujukan="";
            if(nameNode.path("code").asText().equals("200")){
                JsonNode response=mapper.readTree(api.Decrypt(root.path("response").asText(),utc)).path("rujukan");
                rujukan=response.path("noKunjungan").asText();
            }
            skdp_rajal=Sequel.cariIsi("select bridging_surat_kontrol_bpjs.no_surat from bridging_sep "+
                "inner join bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep=bridging_sep.no_sep "+
                "where bridging_surat_kontrol_bpjs.tgl_rencana = CURDATE() and bridging_sep.jnspelayanan = '2'"+
                "and bridging_sep.no_kartu = ? "+
                "order by bridging_surat_kontrol_bpjs.tgl_rencana",noKartu);
            skdp_ranap=Sequel.cariIsi("select bridging_surat_kontrol_bpjs.no_surat from bridging_sep "+
                "inner join bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep=bridging_sep.no_sep "+
                "where bridging_surat_kontrol_bpjs.tgl_rencana = CURDATE() and bridging_sep.jnspelayanan = '1' "+
                "and bridging_sep.no_kartu = ? "+
                "order by bridging_surat_kontrol_bpjs.tgl_rencana",noKartu);

            if(!skdp_rajal.equals("")&&skdp_ranap.equals("")&&!rujukan.equals("")){
                // Kontrol rajal
                noRujukan=rujukan;
                noSKDP=skdp_rajal;
                tujuanKunjungan=2;
            }else if(skdp_rajal.equals("")&&!skdp_ranap.equals("")){
                // Kontrol Post Ranap
                noRujukan=rujukan;
                noSKDP=skdp_ranap;
                tujuanKunjungan=3;
            }else if(skdp_rajal.equals("")&&skdp_ranap.equals("")&&!rujukan.equals("")){
                if(Sequel.cariInteger("SELECT COUNT(bs.no_rujukan) AS jml_sep FROM bridging_sep bs WHERE bs.no_rujukan =?",rujukan)>0){
                    // Internal
                    tujuanKunjungan=1;
                }else{
                    // FKTP
                    tujuanKunjungan=0;
                }
                noRujukan=rujukan;
            }
        } catch (Exception e) {
            System.out.println("Gagal mencari rujukan"+e);
            catatGangguan(e);
        }
    }

    /**
     * Setara tombol Simpan di dialog.
     * @return pesan untuk petugas bila gagal, null bila sukses atau tidak perlu dikirim
     */
    private String validasiDanSimpan(){
        if(noRawat.trim().equals("")||nmPasien.trim().equals("")){
            return "Data pasien tidak ditemukan.";
        }else if(noKartu.trim().equals("")){
            return "Nomor Kartu BPJS pasien masih kosong.";
        }else if(kdDPJP.trim().equals("")||nmDPJP.trim().equals("")){
            return "DPJP belum dimapping ke dokter BPJS.";
        }else if(Sequel.cariInteger("select count(no_sep) from bridging_sep where no_rawat = ?",noRawat)>0){
            return "No Rawat tersebut sudah diterbitkan SEP, silahkan cek kembali data !";
        }else if(!noSKDP.equals("")&&tanggalSEP.equals(Sequel.cariIsi("SELECT SUBSTRING(surkon.created_at, 1,10) AS tgl_surat FROM bridging_surat_kontrol_bpjs surkon WHERE surkon.no_surat =?",noSKDP))){
            return "Tanggal terbit SKDP sama dengan Tanggal Rencana terbit SEP, Silahkan hubungi PIC BPJS";
        }else if(nmPoli.toLowerCase().contains("darurat")){
            return null;
        }else if(Sequel.cariInteger("select count(bridging_sep.no_kartu) from bridging_sep where bridging_sep.no_kartu=? and bridging_sep.jnspelayanan='2' and bridging_sep.tglsep like ? and bridging_sep.nmpolitujuan=?",
                no_peserta,"%"+tanggalSEP+"%",nmPoli)>=1){
            return "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!";
        }else if(ADDANTRIANAPIMOBILEJKN.equals("yes")){
            if(simpanAntrianOnSite()){
                if(tanpaReferensi){
                    // di dialog petugas melihat isian kosong; di mode otomatis harus diberi tahu
                    return "Antrean tidak dikirim karena No. Rujukan dan No. SKDP tidak ditemukan.";
                }
                System.out.println("Antrol Otomatis : Sukses, Antrian online BPJS berhasil dibuat. No.Rawat "+noRawat);
                return null;
            }else{
                return "Maaf, antrian mobile JKN gagal dibuat. Silahkan cek jadwal dokter / Nomor Referensi..!!";
            }
        }
        return null;
    }

    /** Setara SimpanAntrianOnSite di dialog. */
    private boolean simpanAntrianOnSite(){
        ApiMobileJKN apiMobileJKN=new ApiMobileJKN();
        boolean statusantrean=true;
        if(Sequel.cariInteger("select count(referensi_mobilejkn_bpjs.no_rawat) from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.no_rawat=?",noRawat)==0
                ||Sequel.cariInteger("select count(referensi_mobilejkn_bpjs.no_rawat) from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.status ='Batal' and referensi_mobilejkn_bpjs.no_rawat=?",noRawat)>0){
            if((!noRujukan.equals(""))||(!noSKDP.equals(""))){
                String jeniskunjungan=JENIS_KUNJUNGAN[tujuanKunjungan];
                String hari="",jammulai="",jamselesai="",respon="200";
                int kuota=0;
                Date parsedDate=null;
                try {
                    switch (day) {
                        case 1: hari="AKHAD"; break;
                        case 2: hari="SENIN"; break;
                        case 3: hari="SELASA"; break;
                        case 4: hari="RABU"; break;
                        case 5: hari="KAMIS"; break;
                        case 6: hari="JUMAT"; break;
                        case 7: hari="SABTU"; break;
                        default: break;
                    }
                    String tanggal=LocalDate.now().toString();
                    if(Sequel.cariInteger("SELECT COUNT(ln.tanggal) FROM libur_nasional ln WHERE ln.tanggal =?",tanggal)>0){
                        hari="LIBNAS";
                    }

                    PreparedStatement ps=koneksi.prepareStatement("select jadwal.jam_mulai,jadwal.jam_selesai,jadwal.kuota from jadwal where jadwal.hari_kerja=? and jadwal.kd_poli=? and jadwal.kd_dokter=?");
                    ResultSet rs=null;
                    try {
                        ps.setString(1,hari);
                        ps.setString(2,kodepolireg);
                        ps.setString(3,kodedokterreg);
                        rs=ps.executeQuery();
                        if(rs.next()){
                            jammulai=rs.getString("jam_mulai");
                            jamselesai=rs.getString("jam_selesai");
                            kuota=rs.getInt("kuota");

                            String datajam=Sequel.cariIsi("select DATE_ADD(concat('"+tanggalSEP+"',' ','"+jammulai+"'),INTERVAL "+(Integer.parseInt(nomorreg)*interval)+" SECOND) ");
                            parsedDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datajam);
                        }else{
                            statusantrean=false;
                            System.out.println("Jadwal tidak ditemukan...!");
                        }
                    } catch (Exception e) {
                        statusantrean=false;
                        System.out.println("Notif : "+e);
                    } finally{
                        if(rs!=null){
                            rs.close();
                        }
                        if(ps!=null){
                            ps.close();
                        }
                    }

                    if(!noRujukan.equals("")){
                        try {
                            JsonNode nameNode=kirimAntrean(apiMobileJKN,noRujukan,jeniskunjungan,jammulai,jamselesai,kuota,parsedDate);
                            respon=nameNode.path("code").asText();
                            System.out.println("respon WS BPJS Kirim Pakai NoRujukan : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                            if(nameNode.path("code").asText().equals("201")){
                                statusantrean=false;
                            }else if(nameNode.path("code").asText().equals("208")||nameNode.path("code").asText().equals("200")){
                                statusantrean=true;
                                if(nameNode.path("code").asText().equals("200")){
                                    simpanReferensi(jeniskunjungan);
                                }
                            }
                        } catch (Exception e) {
                            statusantrean=false;
                            System.out.println("Notif No.Rujuk : "+e);
                            catatGangguan(e);
                        }
                    }

                    if(respon.equals("201")){
                        if(!noSKDP.equals("")){
                            try {
                                JsonNode nameNode=kirimAntrean(apiMobileJKN,noSKDP,jeniskunjungan,jammulai,jamselesai,kuota,parsedDate);
                                System.out.println("respon WS BPJS Kirim Pakai SKDP : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                if(nameNode.path("code").asText().equals("201")){
                                    statusantrean=false;
                                }else if(nameNode.path("code").asText().equals("208")||nameNode.path("code").asText().equals("200")){
                                    statusantrean=true;
                                    if(nameNode.path("code").asText().equals("200")){
                                        simpanReferensi(jeniskunjungan);
                                    }
                                }
                            } catch (Exception e) {
                                statusantrean=false;
                                System.out.println("Notif SKDP : "+e);
                                catatGangguan(e);
                            }
                        }
                    }
                } catch (Exception e) {
                    statusantrean=false;
                    System.out.println("Notif : "+e);
                }
            }else{
                tanpaReferensi=true;
            }
        }
        return statusantrean;
    }

    /** POST antrean/add dengan nomor referensi tertentu (rujukan atau SKDP), isi JSON sama dengan dialog. */
    private JsonNode kirimAntrean(ApiMobileJKN apiMobileJKN,String nomorreferensi,String jeniskunjungan,
            String jammulai,String jamselesai,int kuota,Date parsedDate) throws Exception {
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-cons-id",consIdMobileJKN);
        String utc=String.valueOf(apiMobileJKN.GetUTCdatetimeAsString());
        headers.add("x-timestamp",utc);
        headers.add("x-signature",apiMobileJKN.getHmac(utc));
        headers.add("user_key",userKeyMobileJKN);

        String requestJson="{"+
                "\"kodebooking\": \""+noRawat+"\","+
                "\"jenispasien\": \"JKN\","+
                "\"nomorkartu\": \""+noKartu+"\","+
                "\"nik\": \""+nik+"\","+
                "\"nohp\": \""+noTelp+"\","+
                "\"kodepoli\": \""+kdPoli+"\","+
                "\"namapoli\": \""+nmPoli+"\","+
                "\"pasienbaru\": 0,"+
                "\"norm\": \""+noRM+"\","+
                "\"tanggalperiksa\": \""+tanggalSEP+"\","+
                "\"kodedokter\": "+kdDPJP+","+
                "\"namadokter\": \""+nmDPJP+"\","+
                "\"jampraktek\": \""+jammulai.substring(0,5)+"-"+jamselesai.substring(0,5)+"\","+
                "\"jeniskunjungan\": "+jeniskunjungan+","+
                "\"nomorreferensi\": \""+nomorreferensi+"\","+
                "\"nomorantrean\": \""+nomorreg+"\","+
                "\"angkaantrean\": "+Integer.parseInt(nomorreg)+","+
                "\"estimasidilayani\": "+parsedDate.getTime()+","+
                "\"sisakuotajkn\": "+(kuota-Integer.parseInt(nomorreg))+","+
                "\"kuotajkn\": "+kuota+","+
                "\"sisakuotanonjkn\": "+(kuota-Integer.parseInt(nomorreg))+","+
                "\"kuotanonjkn\": "+kuota+","+
                "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""+
            "}";
        System.out.println("JSON : "+requestJson+"\n");
        HttpEntity requestEntity=new HttpEntity(requestJson,headers);
        String URL=urlMobileJKN+"/antrean/add";
        System.out.println("URL : "+URL);
        JsonNode root=mapper.readTree(pasangWaktuTunggu(apiMobileJKN.getRest()).exchange(URL,HttpMethod.POST,requestEntity,String.class).getBody());
        return root.path("metadata");
    }

    /**
     * Pasang batas waktu tunggu pada RestTemplate dari ApiBPJS/ApiMobileJKN.getRest().
     * Hanya request factory yang diganti (SSL sama dengan getRest), interceptor pencatat trackerjson tetap dipakai.
     */
    private RestTemplate pasangWaktuTunggu(RestTemplate rest) throws Exception {
        SSLContext sslContext=SSLContext.getInstance("SSL");
        TrustManager[] trustManagers={
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkServerTrusted(X509Certificate[] chain,String authType) {}
                public void checkClientTrusted(X509Certificate[] chain,String authType) {}
            }
        };
        sslContext.init(null,trustManagers,new SecureRandom());
        SSLSocketFactory sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpComponentsClientHttpRequestFactory factory=new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(new Scheme("https",443,sslFactory));
        // spring-web yang terpakai versi lama (tanpa setConnectTimeout), jadi diatur lewat parameter HttpClient
        HttpConnectionParams.setConnectionTimeout(factory.getHttpClient().getParams(),WAKTU_TUNGGU_KONEKSI);
        HttpConnectionParams.setSoTimeout(factory.getHttpClient().getParams(),WAKTU_TUNGGU_RESPON);
        rest.setRequestFactory(new BufferingClientHttpRequestFactory(factory));
        return rest;
    }

    /** Catat jenis gangguan BPJS dari exception: timeout atau tidak bisa dihubungi. */
    private void catatGangguan(Throwable e){
        for(Throwable t=e;t!=null;t=t.getCause()){
            if(t instanceof InterruptedIOException){
                gangguanBpjs="tidak merespon dalam "+(WAKTU_TUNGGU_RESPON/1000)+" detik";
                return;
            }
        }
        if(e instanceof ResourceAccessException){
            gangguanBpjs="tidak dapat dihubungi";
        }
    }

    private void simpanReferensi(String jeniskunjungan){
        Sequel.menyimpan("referensi_antrean_online",
            "'"+noRawat+"','"+noRM+"','"+noKartu+"','"+tanggalSEP+"','"+kdPoli+"','"+kdDPJP+"','"+jeniskunjungan+"','"+noRujukan+"','"+noSKDP+"','0000-00-00 00:00:00',now()");
    }
}
