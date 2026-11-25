/*
 * Logika Rujukan Pasien SATUSEHAT (Rawat Inap & Gawat Darurat).
 * Mengikuti Postman resmi Kemenkes "30. Use Case - Rujukan Pasien" (V2.2, 30 Juni 2026),
 * folder "Contoh Rujukan / 02. Rawat Inap" dan "03. Rawat Darurat".
 *
 * Alur Faskes Perujuk:
 *  1. POST Task "referral-pre-request"         -> server membalas Questionnaire kriteria rujukan
 *                                                  (dan jejaring wilayah) di Task.contained
 *  2. POST Task "request-referral-candidate"   -> jawaban kuesioner (QuestionnaireResponse contained),
 *                                                  server membalas daftar kandidat faskes di Task.output
 *  3. POST Bundle transaction (meta.tag referral-approval): satu Task "referral-approval-request"
 *     per faskes tujuan + CarePlan rencana rujukan (Rawat Inap ditambah Task respon kandidat)
 *  4. GET Task/{id} untuk melihat respon faskes tujuan (output response-referral-task accepted/rejected),
 *     PATCH status cancelled untuk membatalkan
 *  5. POST ServiceRequest rujukan ke faskes yang menerima -> nomor rujukan SATUSEHAT
 *     (identifier http://sys-ids.kemkes.go.id/referral-number-satusehat)
 *
 * Alur Faskes Rujukan (penerima):
 *  1. GET Task?owner={org}&code=referral-approval-request&_include=Task:based-on
 *  2. PATCH Task status completed + output accepted/rejected
 *  3. GET ServiceRequest berdasarkan nomor rujukan, lihat supportingInfo
 *  4. Hubungkan ServiceRequest ke Encounter kunjungan (PATCH Encounter.basedOn)
 *
 * Spring yang dimuat (3.1.0.M2) belum mengenal HTTP PATCH, sehingga seluruh request di kelas ini
 * memakai Apache HttpClient langsung dengan SSL yang sama seperti bridging.ApiSatuSehat.
 */

package modif;

import bridging.ApiSatuSehat;
import bridging.SatuSehatCekNIK;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class SatuSehatRujukan {
    public static final String JENIS_RANAP="Rawat Inap",JENIS_IGD="Gawat Darurat";
    public static final String STATUS_PRA="Pra Permintaan",STATUS_KANDIDAT="Pencarian Kandidat",
            STATUS_MENUNGGU="Menunggu Persetujuan",STATUS_DISETUJUI="Disetujui",
            STATUS_TERKIRIM="Rujukan Terkirim",STATUS_BATAL="Dibatalkan";
    public static final String REF_KELOMPOK_LAYANAN="Kelompok Layanan",REF_SPESIALISASI="Spesialisasi Layanan",
            REF_TENAGA="Jenis Tenaga Medis";
    public static final String SYS_KEMKES="http://terminology.kemkes.go.id",SYS_SNOMED="http://snomed.info/sct",
            SYS_ICD10="http://hl7.org/fhir/sid/icd-10",SYS_SPESIALISASI="http://terminology.kemkes.go.id/CodeSystem/clinical-speciality",
            SYS_NO_RUJUKAN_SATUSEHAT="http://sys-ids.kemkes.go.id/referral-number-satusehat",
            SYS_NO_RUJUKAN_PCARE="http://sys-ids.kemkes.go.id/referral-number-pcare";
    /** id QuestionnaireResponse contained, sama seperti contoh Postman */
    public static final String ID_QR_KRITERIA="123456789",ID_QR_WILAYAH="123456788";
    private static final int WAKTU_TUNGGU_KONEKSI=15000,WAKTU_TUNGGU_RESPON=60000;
    private static final long UMUR_TOKEN=30L*60L*1000L;

    private final ApiSatuSehat api=new ApiSatuSehat();
    private final ObjectMapper mapper=new ObjectMapper();
    private final Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private String link="",idOrg="",namaOrg="",idOrgBpjs="",token="";
    private long waktuToken=0;

    /** Hasil satu request ke SATUSEHAT. */
    public static class Respon {
        public final int kode;
        public final String isi;
        public final JsonNode json;

        Respon(int kode,String isi,JsonNode json){
            this.kode=kode;
            this.isi=isi==null?"":isi;
            this.json=json;
        }

        public boolean sukses(){
            return kode>=200&&kode<300&&json!=null;
        }

        /** Pesan error dari OperationOutcome, atau potongan isi balasan. */
        public String pesan(){
            StringBuilder hasil=new StringBuilder();
            if(json!=null){
                for(JsonNode issue:json.path("issue")){
                    String teks=issue.path("details").path("text").asText();
                    if(teks.equals("")){
                        teks=issue.path("diagnostics").asText();
                    }
                    if(teks.equals("")){
                        teks=issue.path("code").asText();
                    }
                    if(!teks.equals("")){
                        hasil.append("- ").append(teks).append("\n");
                    }
                }
            }
            if(hasil.length()==0){
                hasil.append("HTTP ").append(kode).append(" : ").append(isi.length()>500?isi.substring(0,500)+"...":isi);
            }
            return hasil.toString().trim();
        }
    }

    /** Diagnosa pasien (ICD 10) beserta ID Condition SATUSEHAT bila sudah terkirim. */
    public static class Diagnosa {
        public String kode="",nama="",idCondition="";
    }

    /** Resource SATUSEHAT milik kunjungan yang bisa dilampirkan sebagai supportingInfo. */
    public static class DataPendukung {
        public String resource="",id="",keterangan="";
        public boolean dipilih=true;
    }

    /** Kandidat faskes rujukan hasil Task pencarian kandidat. */
    public static class Kandidat {
        public String idOrg="",nama="",jarak="",waktu="",strata="",kodeBpjs="",bed="",bedDetail="";
        public String idTask="",idCarePlan="",status="",respon="";
        public JsonNode output;
        public boolean dipilih=false;
    }

    /** Data pasien dan kunjungan yang dibutuhkan rujukan. */
    public static class DataPasien {
        public String noRawat="",noRM="",nama="",nik="",idPasien="",idEncounter="",
                kdDokter="",nmDokter="",nikDokter="",idDokter="",noPeserta="",caraBayar="",noRujukanBpjs="";
        public final List<Diagnosa> diagnosa=new ArrayList<>();
        public final List<DataPendukung> pendukung=new ArrayList<>();

        public Diagnosa diagnosaUtama(){
            return diagnosa.isEmpty()?null:diagnosa.get(0);
        }

        public boolean bpjs(){
            return caraBayar.toUpperCase().contains("BPJS")&&!noPeserta.trim().equals("");
        }
    }

    /** Proses rujukan keluar yang tersimpan di tabel satu_sehat_rujukan. */
    public static class Proses {
        public String noRawat="",tglRujukan="",jenis="",kdDokter="",idTaskPra="",idTaskKandidat="",idTaskResponKandidat="",
                idCarePlan="",idServiceRequest="",noRujukanSatuSehat="",idOrgTujuan="",nmOrgTujuan="",status="";
    }

    public SatuSehatRujukan(){
        try {
            link=koneksiDB.URLFHIRSATUSEHAT();
            idOrg=koneksiDB.IDSATUSEHAT();
            idOrgBpjs=koneksiDB.IDORGBPJSSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Rujukan SATUSEHAT : "+e);
        }
        try {
            ps=koneksi.prepareStatement("select setting.nama_instansi from setting");
            try {
                rs=ps.executeQuery();
                if(rs.next()){
                    namaOrg=rs.getString(1);
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        if(link==null){
            link="";
        }
        if(idOrg==null){
            idOrg="";
        }
        if(idOrgBpjs==null){
            idOrgBpjs="";
        }
        if(namaOrg==null){
            namaOrg="";
        }
    }

    public String getIdOrg(){
        return idOrg;
    }

    public String getNamaOrg(){
        return namaOrg;
    }

    public ObjectMapper getMapper(){
        return mapper;
    }

    // ===================================================================================
    // HTTP
    // ===================================================================================

    private synchronized String ambilToken(){
        if(token==null||token.equals("")||System.currentTimeMillis()-waktuToken>UMUR_TOKEN){
            token=api.TokenSatuSehat();
            waktuToken=System.currentTimeMillis();
        }
        return token==null?"":token;
    }

    /** PATCH belum tersedia sebagai kelas di semua versi HttpClient yang ada di lib. */
    private static class HttpPatchFhir extends HttpEntityEnclosingRequestBase {
        HttpPatchFhir(String url){
            super();
            setURI(java.net.URI.create(url));
        }

        @Override
        public String getMethod(){
            return "PATCH";
        }
    }

    private static class HttpPutFhir extends HttpEntityEnclosingRequestBase {
        HttpPutFhir(String url){
            super();
            setURI(java.net.URI.create(url));
        }

        @Override
        public String getMethod(){
            return "PUT";
        }
    }

    /**
     * Kirim request ke FHIR SATUSEHAT.
     * @param metode GET/POST/PUT/PATCH
     * @param path   path setelah base url FHIR (misal "/Task"), "" untuk Bundle transaction
     * @param body   isi JSON, null untuk GET
     */
    public Respon kirim(String metode,String path,JsonNode body){
        String url=link+path;
        String isi=null;
        int kode=0;
        DefaultHttpClient klien=null;
        try {
            SSLContext sslContext=SSLContext.getInstance("TLSv1.2");
            TrustManager[] trustManagers={
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {return null;}
                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0,String arg1){}
                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0,String arg1){}
                }
            };
            sslContext.init(null,trustManagers,new SecureRandom());
            klien=new DefaultHttpClient();
            klien.getConnectionManager().getSchemeRegistry().register(
                    new Scheme("https",443,new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)));
            HttpConnectionParams.setConnectionTimeout(klien.getParams(),WAKTU_TUNGGU_KONEKSI);
            HttpConnectionParams.setSoTimeout(klien.getParams(),WAKTU_TUNGGU_RESPON);

            HttpRequestBase request;
            switch (metode) {
                case "POST":
                    request=new HttpPost(url);
                    break;
                case "PUT":
                    request=new HttpPutFhir(url);
                    break;
                case "PATCH":
                    request=new HttpPatchFhir(url);
                    break;
                default:
                    request=new HttpGet(url);
                    break;
            }
            request.setHeader("Authorization","Bearer "+ambilToken());
            request.setHeader("Accept","application/json");
            System.out.println("URL : "+url);
            if(body!=null&&request instanceof HttpEntityEnclosingRequestBase){
                String json=mapper.writeValueAsString(body);
                System.out.println("Request JSON : "+json);
                StringEntity entity=new StringEntity(json,"UTF-8");
                ((HttpEntityEnclosingRequestBase)request).setEntity(entity);
                request.setHeader("Content-Type",metode.equals("PATCH")?"application/json-patch+json":"application/json");
            }
            HttpResponse response=klien.execute(request);
            kode=response.getStatusLine().getStatusCode();
            isi=response.getEntity()==null?"":EntityUtils.toString(response.getEntity(),"UTF-8");
            System.out.println("Result JSON : "+isi);
        } catch (Exception e) {
            System.out.println("Notifikasi Rujukan SATUSEHAT : "+e);
            return new Respon(0,"Gagal terhubung ke SATUSEHAT : "+e.getMessage(),null);
        } finally{
            if(klien!=null){
                klien.getConnectionManager().shutdown();
            }
        }
        JsonNode json=null;
        try {
            if(isi!=null&&!isi.trim().equals("")){
                json=mapper.readTree(isi);
            }
        } catch (Exception e) {
            json=null;
        }
        if(kode==401){
            token="";
        }
        return new Respon(kode,isi,json);
    }

    // ===================================================================================
    // Data pasien & kunjungan
    // ===================================================================================

    /**
     * Membaca data pasien, dokter, encounter, diagnosa dan data pendukung kunjungan dari database,
     * lalu mencari IHS Patient & Practitioner. Dipanggil dari thread background.
     * @param kdDokter dokter pilihan form pemanggil, boleh kosong (dipakai DPJP ranap / dokter registrasi)
     */
    public DataPasien muatPasien(String noRawat,String kdDokter){
        DataPasien data=new DataPasien();
        data.noRawat=noRawat;
        try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,pasien.no_peserta,reg_periksa.kd_dokter,penjab.png_jawab "+
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,noRawat);
                rs=ps.executeQuery();
                if(rs.next()){
                    data.noRM=kosong(rs.getString(1));
                    data.nama=kosong(rs.getString(2));
                    data.nik=kosong(rs.getString(3));
                    data.noPeserta=kosong(rs.getString(4));
                    data.kdDokter=kosong(rs.getString(5));
                    data.caraBayar=kosong(rs.getString(6));
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                tutup();
            }

            if(kdDokter!=null&&!kdDokter.trim().equals("")){
                data.kdDokter=kdDokter.trim();
            }else{
                String dpjp=cariIsi("select dpjp_ranap.kd_dokter from dpjp_ranap where dpjp_ranap.no_rawat=? limit 1",noRawat);
                if(!dpjp.equals("")){
                    data.kdDokter=dpjp;
                }
            }
            ps=koneksi.prepareStatement(
                    "select dokter.nm_dokter,pegawai.no_ktp from dokter inner join pegawai on dokter.kd_dokter=pegawai.nik where dokter.kd_dokter=?");
            try {
                ps.setString(1,data.kdDokter);
                rs=ps.executeQuery();
                if(rs.next()){
                    data.nmDokter=kosong(rs.getString(1));
                    data.nikDokter=kosong(rs.getString(2));
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                tutup();
            }

            data.idEncounter=cariIsi("select satu_sehat_encounter.id_encounter from satu_sehat_encounter where satu_sehat_encounter.no_rawat=?",noRawat);
            data.noRujukanBpjs=cariIsi(
                    "select bridging_rujukan_bpjs.no_rujukan from bridging_rujukan_bpjs inner join bridging_sep on bridging_rujukan_bpjs.no_sep=bridging_sep.no_sep "+
                    "where bridging_sep.no_rawat=? order by bridging_rujukan_bpjs.tglRujukan desc limit 1",noRawat);

            // diagnosa: Ranap didahulukan, lalu prioritas; kode yang sama hanya sekali
            Set<String> sudah=new LinkedHashSet<>();
            ps=koneksi.prepareStatement(
                    "select diagnosa_pasien.kd_penyakit,penyakit.nm_penyakit from diagnosa_pasien inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "+
                    "where diagnosa_pasien.no_rawat=? order by if(diagnosa_pasien.status='Ranap',0,1),diagnosa_pasien.prioritas");
            try {
                ps.setString(1,noRawat);
                rs=ps.executeQuery();
                while(rs.next()){
                    if(sudah.add(rs.getString(1))){
                        Diagnosa diagnosa=new Diagnosa();
                        diagnosa.kode=kosong(rs.getString(1));
                        diagnosa.nama=kosong(rs.getString(2));
                        data.diagnosa.add(diagnosa);
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                tutup();
            }
            for(Diagnosa diagnosa:data.diagnosa){
                diagnosa.idCondition=cariIsi(
                        "select satu_sehat_condition.id_condition from satu_sehat_condition where satu_sehat_condition.no_rawat=? and satu_sehat_condition.kd_penyakit=? "+
                        "and satu_sehat_condition.id_condition<>'' limit 1",noRawat,diagnosa.kode);
            }
            muatPendukung(data);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }

        SatuSehatCekNIK cekNIK=new SatuSehatCekNIK();
        if(!data.nik.equals("")){
            data.idPasien=kosong(cekNIK.tampilIDPasien(data.nik));
        }
        if(!data.nikDokter.equals("")){
            data.idDokter=kosong(cekNIK.tampilIDParktisi(data.nikDokter));
        }
        return data;
    }

    /** Mengumpulkan resource SATUSEHAT kunjungan yang sudah terkirim (TTV, alergi, prosedur, penunjang, dst). */
    private void muatPendukung(DataPasien data){
        String[][] daftar={
            {"satu_sehat_observationttvkesadaran","id_observation","Observation","Asesmen Awal - Pemeriksaan Fisik - Tingkat Kesadaran"},
            {"satu_sehat_observationttvgcs","id_observation","Observation","Asesmen Awal - Pemeriksaan Fisik - GCS"},
            {"satu_sehat_observationttvtb","id_observation","Observation","Asesmen Awal - Antropometri - Tinggi Badan"},
            {"satu_sehat_observationttvbb","id_observation","Observation","Asesmen Awal - Antropometri - Berat Badan"},
            {"satu_sehat_observationttvlp","id_observation","Observation","Asesmen Awal - Antropometri - Lingkar Perut"},
            {"satu_sehat_observationttvtensi","id_observation","Observation","Asesmen Awal - Vital Sign - Tekanan Darah"},
            {"satu_sehat_observationttvnadi","id_observation","Observation","Asesmen Awal - Vital Sign - Nadi"},
            {"satu_sehat_observationttvsuhu","id_observation","Observation","Asesmen Awal - Vital Sign - Suhu"},
            {"satu_sehat_observationttvrespirasi","id_observation","Observation","Asesmen Awal - Vital Sign - Pernafasan"},
            {"satu_sehat_observationttvspo2","id_observation","Observation","Asesmen Awal - Vital Sign - Saturasi Oksigen"},
            {"satu_sehat_allergy_intolerance","id_allergy_intolerance","AllergyIntolerance","Anamnesis - Riwayat Alergi"},
            {"satu_sehat_clinicalimpression","id_clinicalimpression","ClinicalImpression","Asesmen - Clinical Impression"},
            {"satu_sehat_careplan","id_careplan","CarePlan","Rencana Perawatan"}
        };
        for(String[] tabel:daftar){
            tambahPendukung(data,"select "+tabel[0]+"."+tabel[1]+",concat(date_format("+tabel[0]+".tgl_perawatan,'%d-%m-%Y'),' ',"+tabel[0]+".jam_rawat) from "+tabel[0]+
                    " where "+tabel[0]+".no_rawat=? and "+tabel[0]+"."+tabel[1]+"<>'' order by "+tabel[0]+".tgl_perawatan desc,"+tabel[0]+".jam_rawat desc",tabel[2],tabel[3]);
        }
        tambahPendukung(data,"select satu_sehat_condition.id_condition,concat(satu_sehat_condition.kd_penyakit,' ',penyakit.nm_penyakit) from satu_sehat_condition "+
                "inner join penyakit on satu_sehat_condition.kd_penyakit=penyakit.kd_penyakit where satu_sehat_condition.no_rawat=? and satu_sehat_condition.id_condition<>''",
                "Condition","Diagnosis");
        tambahPendukung(data,"select satu_sehat_procedure.id_procedure,concat(satu_sehat_procedure.kode,' ',icd9.deskripsi_panjang) from satu_sehat_procedure "+
                "inner join icd9 on satu_sehat_procedure.kode=icd9.kode where satu_sehat_procedure.no_rawat=? and satu_sehat_procedure.id_procedure<>''",
                "Procedure","Tindakan/Prosedur Medis");
        tambahPendukung(data,"select satu_sehat_medicationdispense.id_medicationdispanse,concat(databarang.nama_brng,' ',date_format(satu_sehat_medicationdispense.tgl_perawatan,'%d-%m-%Y'),' ',"+
                "satu_sehat_medicationdispense.jam) from satu_sehat_medicationdispense inner join databarang on satu_sehat_medicationdispense.kode_brng=databarang.kode_brng "+
                "where satu_sehat_medicationdispense.no_rawat=? and satu_sehat_medicationdispense.id_medicationdispanse<>'' "+
                "order by satu_sehat_medicationdispense.tgl_perawatan desc,satu_sehat_medicationdispense.jam desc",
                "MedicationDispense","Pengeluaran Obat");
        tambahPendukung(data,"select satu_sehat_observation_lab.id_observation,concat(jns_perawatan_lab.nm_perawatan,' (',permintaan_lab.noorder,')') from satu_sehat_observation_lab "+
                "inner join permintaan_lab on satu_sehat_observation_lab.noorder=permintaan_lab.noorder "+
                "inner join jns_perawatan_lab on satu_sehat_observation_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw "+
                "where permintaan_lab.no_rawat=? and satu_sehat_observation_lab.id_observation<>''",
                "Observation","Pemeriksaan Penunjang - Laboratorium - Hasil");
        tambahPendukung(data,"select satu_sehat_diagnosticreport_lab.id_diagnosticreport,concat(jns_perawatan_lab.nm_perawatan,' (',permintaan_lab.noorder,')') from satu_sehat_diagnosticreport_lab "+
                "inner join permintaan_lab on satu_sehat_diagnosticreport_lab.noorder=permintaan_lab.noorder "+
                "inner join jns_perawatan_lab on satu_sehat_diagnosticreport_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw "+
                "where permintaan_lab.no_rawat=? and satu_sehat_diagnosticreport_lab.id_diagnosticreport<>''",
                "DiagnosticReport","Pemeriksaan Penunjang - Laboratorium - Laporan");
        tambahPendukung(data,"select satu_sehat_diagnosticreport_radiologi.id_diagnosticreport,concat(jns_perawatan_radiologi.nm_perawatan,' (',permintaan_radiologi.noorder,')') "+
                "from satu_sehat_diagnosticreport_radiologi inner join permintaan_radiologi on satu_sehat_diagnosticreport_radiologi.noorder=permintaan_radiologi.noorder "+
                "inner join jns_perawatan_radiologi on satu_sehat_diagnosticreport_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw "+
                "where permintaan_radiologi.no_rawat=? and satu_sehat_diagnosticreport_radiologi.id_diagnosticreport<>''",
                "DiagnosticReport","Pemeriksaan Penunjang - Radiologi - Laporan");
    }

    private void tambahPendukung(DataPasien data,String sql,String resource,String label){
        try {
            ps=koneksi.prepareStatement(sql);
            try {
                ps.setString(1,data.noRawat);
                rs=ps.executeQuery();
                while(rs.next()){
                    DataPendukung pendukung=new DataPendukung();
                    pendukung.resource=resource;
                    pendukung.id=kosong(rs.getString(1));
                    pendukung.keterangan=label+" - "+kosong(rs.getString(2));
                    data.pendukung.add(pendukung);
                }
            } catch (Exception e) {
                // tabel bisa belum ada di database lama, cukup dilewati
                System.out.println("Notifikasi Data Pendukung "+resource+" : "+e);
            } finally{
                tutup();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    // ===================================================================================
    // Pembentuk resource FHIR (tanpa I/O)
    // ===================================================================================

    /** Waktu sekarang format FHIR dengan zona waktu lokal, misal 2026-11-18T10:15:00+07:00 */
    public static String waktuSekarang(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
    }

    /** "2026-11-18 10:15" atau "2026-11-18 10:15:00" -> format FHIR dengan zona lokal. */
    public static String waktuFhir(String waktu) throws Exception{
        String teks=waktu.trim();
        if(teks.length()==16){
            teks=teks+":00";
        }
        SimpleDateFormat baca=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        baca.setLenient(false);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(baca.parse(teks));
    }

    /** Identifier lokal unik per langkah, misal 20261118000001-PRA-261118101500 */
    public static String idLokal(String noRawat,String langkah){
        return noRawat.replaceAll("[^0-9A-Za-z]","")+"-"+langkah+"-"+new SimpleDateFormat("yyMMddHHmmss").format(new Date());
    }

    private static ObjectNode coding(ObjectNode induk,String nama,String system,String code,String display){
        ObjectNode coding=induk.putObject(nama);
        coding.put("system",system);
        coding.put("code",code);
        coding.put("display",display);
        return coding;
    }

    private static ObjectNode codeable(ObjectNode induk,String nama,String system,String code,String display){
        ObjectNode codeable=induk.putObject(nama);
        ObjectNode coding=codeable.putArray("coding").addObject();
        coding.put("system",system);
        coding.put("code",code);
        coding.put("display",display);
        return codeable;
    }

    private static void referensi(ObjectNode induk,String nama,String reference,String display){
        ObjectNode ref=induk.putObject(nama);
        ref.put("reference",reference);
        if(display!=null&&!display.trim().equals("")){
            ref.put("display",display.trim());
        }
    }

    private void identifier(ObjectNode resource,String jenis,String nilai){
        ObjectNode id=resource.putArray("identifier").addObject();
        id.put("system","http://sys-ids.kemkes.go.id/"+jenis+"/"+idOrg);
        id.put("value",nilai);
    }

    public static String kodeManajemen(String jenis){
        return jenis.equals(JENIS_IGD)?"385868005":"737481003";
    }

    public static String namaManajemen(String jenis){
        return jenis.equals(JENIS_IGD)?"Emergency treatment management":"Inpatient care management";
    }

    private static void inputManajemen(ArrayNode input,String jenis){
        ObjectNode item=input.addObject();
        codeable(item,"type",SYS_SNOMED,"119270007","Management procedure");
        coding(item,"valueCoding",SYS_SNOMED,kodeManajemen(jenis),namaManajemen(jenis));
    }

    private static void inputDiagnosa(ArrayNode input,DataPasien data){
        for(int i=0;i<data.diagnosa.size();i++){
            Diagnosa diagnosa=data.diagnosa.get(i);
            ObjectNode item=input.addObject();
            if(i==0){
                codeable(item,"type",SYS_KEMKES,"primary-diagnosis","Primary Diagnosis");
            }else{
                codeable(item,"type",SYS_KEMKES,"secondary-diagnosis","Secondary diagnosis");
            }
            coding(item,"valueCoding",SYS_ICD10,diagnosa.kode,diagnosa.nama);
        }
    }

    private void kepalaTask(ObjectNode task,DataPasien data,String kode,String display,String identitas,String waktu,boolean pasien){
        task.put("resourceType","Task");
        identifier(task,"task",identitas);
        task.put("status","requested");
        task.put("intent","instance-order");
        task.put("priority","routine");
        codeable(task,"code",SYS_KEMKES,kode,display);
        if(pasien){
            referensi(task,"for","Patient/"+data.idPasien,null);
        }
        task.put("authoredOn",waktu);
        task.put("lastModified",waktu);
        referensi(task,"requester","Organization/"+idOrg,null);
        referensi(task,"owner","Organization/"+idOrg,null);
        referensi(task,"encounter","Encounter/"+data.idEncounter,null);
    }

    /** Langkah 1: Task "referral-pre-request". */
    public ObjectNode bangunTaskPraPermintaan(DataPasien data,String jenis,String identitas){
        String waktu=waktuSekarang();
        ObjectNode task=mapper.createObjectNode();
        kepalaTask(task,data,"referral-pre-request","Referral pre request",identitas,waktu,false);
        ArrayNode input=task.putArray("input");
        inputManajemen(input,jenis);
        ObjectNode item=input.addObject();
        Diagnosa utama=data.diagnosaUtama();
        codeable(item,"type",SYS_KEMKES,"primary-diagnosis","Primary Diagnosis");
        coding(item,"valueCoding",SYS_ICD10,utama.kode,utama.nama);
        return task;
    }

    /** QuestionnaireResponse contained untuk Task pencarian kandidat. */
    public ObjectNode bangunJawaban(String id,String questionnaire,DataPasien data,ArrayNode item){
        ObjectNode qr=mapper.createObjectNode();
        qr.put("resourceType","QuestionnaireResponse");
        qr.put("id",id);
        qr.put("questionnaire",questionnaire);
        qr.put("status","completed");
        referensi(qr,"subject","Patient/"+data.idPasien,null);
        referensi(qr,"encounter","Encounter/"+data.idEncounter,null);
        qr.set("item",item);
        return qr;
    }

    /**
     * Langkah 2: Task "request-referral-candidate".
     * @param jawabanKriteria item QuestionnaireResponse kriteria rujukan
     * @param jawabanWilayah  item QuestionnaireResponse jejaring wilayah rujukan
     * @param kelompokLayanan {kode,nama} atau null bila tidak diisi
     */
    public ObjectNode bangunTaskKandidat(DataPasien data,String jenis,String identitas,ArrayNode jawabanKriteria,ArrayNode jawabanWilayah,String[] kelompokLayanan){
        String waktu=waktuSekarang();
        ObjectNode task=mapper.createObjectNode();
        task.put("resourceType","Task");
        ArrayNode contained=task.putArray("contained");
        contained.add(bangunJawaban(ID_QR_KRITERIA,"https://fhir.kemkes.go.id/Questionnaire/Q100",data,jawabanKriteria));
        contained.add(bangunJawaban(ID_QR_WILAYAH,"https://fhir.kemkes.go.id/Questionnaire/Q101",data,jawabanWilayah));
        ObjectNode kepala=mapper.createObjectNode();
        kepalaTask(kepala,data,"request-referral-candidate","Request for referral candidate",identitas,waktu,true);
        task.setAll(kepala);

        ArrayNode input=task.putArray("input");
        ObjectNode item=input.addObject();
        codeable(item,"type",SYS_KEMKES,"referral-criteria","Referral Criteria");
        referensi(item,"valueReference","#"+ID_QR_KRITERIA,"Referral Criteria Response");
        item=input.addObject();
        codeable(item,"type",SYS_KEMKES,"area","Area");
        referensi(item,"valueReference","#"+ID_QR_WILAYAH,"Jejaring Wilayah Rujukan");
        inputManajemen(input,jenis);
        if(kelompokLayanan!=null){
            item=input.addObject();
            codeable(item,"type",SYS_KEMKES,"TK000562","Kelompok Layanan");
            coding(item,"valueCoding",SYS_KEMKES,kelompokLayanan[0],kelompokLayanan[1]);
        }
        inputDiagnosa(input,data);
        return task;
    }

    /** Membaca daftar kandidat faskes dari Task.output "candidate-referral-facility". */
    public static List<Kandidat> bacaKandidat(JsonNode task){
        List<Kandidat> hasil=new ArrayList<>();
        for(JsonNode output:task.path("output")){
            if(!kodePertama(output.path("type")).equals("candidate-referral-facility")){
                continue;
            }
            Kandidat kandidat=new Kandidat();
            kandidat.output=output;
            kandidat.idOrg=idDariReferensi(output.path("valueReference").path("reference").asText());
            kandidat.nama=output.path("valueReference").path("display").asText();
            int tersedia=0;
            StringBuilder detail=new StringBuilder();
            for(JsonNode ext:output.path("extension")){
                for(JsonNode atribut:ext.path("extension")){
                    String url=atribut.path("url").asText();
                    switch (url) {
                        case "distance":
                            kandidat.jarak=atribut.path("valueQuantity").path("value").asText();
                            break;
                        case "estimated-time":
                            kandidat.waktu=atribut.path("valueQuantity").path("value").asText();
                            break;
                        case "strata":
                            kandidat.strata=atribut.path("valueCode").asText();
                            break;
                        case "bpjs-code":
                            kandidat.kodeBpjs=atribut.path("valueCode").asText();
                            break;
                        case "bed-availability":
                            String nama="",kategori="";
                            int total=0,ada=0;
                            for(JsonNode bed:atribut.path("extension")){
                                switch (bed.path("url").asText()) {
                                    case "name":
                                        nama=bed.path("valueString").asText();
                                        break;
                                    case "category":
                                        kategori=bed.path("valueString").asText();
                                        break;
                                    case "total":
                                        total=bed.path("valueInteger").asInt();
                                        break;
                                    case "available":
                                        ada=bed.path("valueInteger").asInt();
                                        break;
                                    default:
                                        break;
                                }
                            }
                            tersedia=tersedia+ada;
                            detail.append(nama).append(kategori.equals("")?"":" ("+kategori+")").append(" : ").append(ada).append("/").append(total).append("\n");
                            break;
                        default:
                            break;
                    }
                }
            }
            kandidat.bed=detail.length()==0?"":String.valueOf(tersedia);
            kandidat.bedDetail=detail.toString().trim();
            hasil.add(kandidat);
        }
        return hasil;
    }

    /**
     * Langkah 3: Bundle transaction Task persetujuan (satu per faskes tujuan) + CarePlan.
     * Rawat Inap juga menyertakan Task respon kandidat (status completed) seperti contoh Postman.
     * Urutan entry: Task faskes tujuan (sesuai urutan daftar), CarePlan, [Task respon kandidat].
     * @param spesialisasi {kode,nama} layanan yang diminta (CarePlan.activity)
     * @param taskKandidat resource Task pencarian kandidat dari server (untuk respon kandidat Rawat Inap)
     */
    public ObjectNode bangunBundlePersetujuan(DataPasien data,String jenis,List<Kandidat> tujuan,String judul,String deskripsi,
            String[] spesialisasi,List<DataPendukung> pendukung,JsonNode taskKandidat,String idTaskKandidat){
        String waktu=waktuSekarang();
        String uuidCarePlan=UUID.randomUUID().toString();
        Diagnosa utama=data.diagnosaUtama();
        ObjectNode bundle=mapper.createObjectNode();
        bundle.put("resourceType","Bundle");
        bundle.put("type","transaction");
        ObjectNode tag=bundle.putObject("meta").putArray("tag").addObject();
        tag.put("system",SYS_KEMKES);
        tag.put("code","referral-approval");
        tag.put("display","Referral approval");
        ArrayNode entry=bundle.putArray("entry");

        int urut=1;
        for(Kandidat kandidat:tujuan){
            ObjectNode task=mapper.createObjectNode();
            task.put("resourceType","Task");
            identifier(task,"task",idLokal(data.noRawat,"PRS"+urut));
            task.putArray("basedOn").addObject().put("reference","urn:uuid:"+uuidCarePlan);
            task.put("status","requested");
            task.put("intent","instance-order");
            task.put("priority","routine");
            codeable(task,"code",SYS_KEMKES,"referral-approval-request","Referral approval request");
            referensi(task,"for","Patient/"+data.idPasien,null);
            task.putObject("executionPeriod").put("start",waktu);
            task.put("authoredOn",waktu);
            task.put("lastModified",waktu);
            referensi(task,"requester","Organization/"+idOrg,null);
            referensi(task,"owner","Organization/"+kandidat.idOrg,null);
            referensi(task,"encounter","Encounter/"+data.idEncounter,null);
            if(utama!=null&&!utama.idCondition.equals("")){
                referensi(task,"reasonReference","Condition/"+utama.idCondition,null);
            }
            ObjectNode item=task.putArray("input").addObject();
            codeable(item,"type",SYS_KEMKES,"referral-task","Referral Task").put("text","Penugasan Task Rujukan");
            referensi(item,"valueReference","Organization/"+kandidat.idOrg,kandidat.nama);
            tambahEntry(entry,UUID.randomUUID().toString(),task,"Task");
            urut++;
        }

        ObjectNode careplan=mapper.createObjectNode();
        careplan.put("resourceType","CarePlan");
        ArrayNode identitas=careplan.putArray("identifier");
        ObjectNode id=identitas.addObject();
        id.put("system","http://sys-ids.kemkes.go.id/careplan/"+idOrg);
        id.put("value",idLokal(data.noRawat,"CP"));
        id=identitas.addObject();
        id.put("system","http://sys-ids.kemkes.go.id/careplan/authoring-organization");
        id.put("value",idOrg);
        careplan.put("status","active");
        careplan.put("intent","plan");
        ArrayNode kategori=careplan.putArray("category");
        ObjectNode kat=kategori.addObject().putArray("coding").addObject();
        if(jenis.equals(JENIS_IGD)){
            kat.put("system",SYS_KEMKES);
            kat.put("code","TK000068");
            kat.put("display","Emergency care plan");
        }else{
            kat.put("system",SYS_SNOMED);
            kat.put("code","736353004");
            kat.put("display","Inpatient care plan");
        }
        kat=kategori.addObject().putArray("coding").addObject();
        kat.put("system",SYS_SNOMED);
        kat.put("code","3457005");
        kat.put("display","Patient referral");
        careplan.put("title",judul);
        careplan.put("description",deskripsi);
        referensi(careplan,"subject","Patient/"+data.idPasien,data.nama);
        referensi(careplan,"encounter","Encounter/"+data.idEncounter,null);
        careplan.put("created",waktu);
        referensi(careplan,"author","Practitioner/"+data.idDokter,data.nmDokter);
        // contoh Postman Gawat Darurat tidak menyertakan addresses & supportingInfo pada CarePlan
        if(!jenis.equals(JENIS_IGD)){
            ArrayNode addresses=careplan.putArray("addresses");
            for(Diagnosa diagnosa:data.diagnosa){
                if(!diagnosa.idCondition.equals("")){
                    addresses.addObject().put("reference","Condition/"+diagnosa.idCondition);
                }
            }
            if(addresses.size()==0){
                careplan.remove("addresses");
            }
            ArrayNode supporting=careplan.putArray("supportingInfo");
            for(DataPendukung item:pendukung){
                if(item.dipilih&&!item.resource.equals("Condition")){
                    ObjectNode ref=supporting.addObject();
                    ref.put("display",item.keterangan);
                    ref.put("reference",item.resource+"/"+item.id);
                }
            }
            if(supporting.size()==0){
                careplan.remove("supportingInfo");
            }
        }
        careplan.putArray("contributor").addObject().put("reference","Organization/"+idOrg);
        ObjectNode detail=careplan.putArray("activity").addObject().putObject("detail");
        detail.put("kind","ServiceRequest");
        codeable(detail,"code",SYS_SPESIALISASI,spesialisasi[0],spesialisasi[1]).put("text","Permintaan Layanan "+spesialisasi[1]);
        detail.put("status","not-started");
        tambahEntry(entry,uuidCarePlan,careplan,"CarePlan");

        if(jenis.equals(JENIS_RANAP)&&taskKandidat!=null&&!taskKandidat.isMissingNode()){
            ObjectNode respon=mapper.createObjectNode();
            respon.put("resourceType","Task");
            if(taskKandidat.has("contained")){
                respon.set("contained",taskKandidat.path("contained").deepCopy());
            }
            identifier(respon,"task",idLokal(data.noRawat,"RSK"));
            respon.put("status","completed");
            respon.put("intent","instance-order");
            respon.put("priority","routine");
            codeable(respon,"code",SYS_KEMKES,"request-referral-candidate","Request for referral candidate");
            respon.putArray("basedOn").addObject().put("reference","Task/"+idTaskKandidat);
            referensi(respon,"for","Patient/"+data.idPasien,null);
            respon.putObject("executionPeriod").put("start",waktu);
            respon.put("authoredOn",waktu);
            respon.put("lastModified",waktu);
            referensi(respon,"requester","Organization/"+idOrg,null);
            referensi(respon,"owner","Organization/"+idOrg,null);
            referensi(respon,"encounter","Encounter/"+data.idEncounter,null);
            if(taskKandidat.has("input")){
                respon.set("input",taskKandidat.path("input").deepCopy());
            }
            ArrayNode output=respon.putArray("output");
            for(Kandidat kandidat:tujuan){
                if(kandidat.output!=null){
                    output.add(kandidat.output.deepCopy());
                }
            }
            tambahEntry(entry,UUID.randomUUID().toString(),respon,"Task");
        }
        return bundle;
    }

    private static void tambahEntry(ArrayNode entry,String uuid,ObjectNode resource,String url){
        ObjectNode item=entry.addObject();
        item.put("fullUrl","urn:uuid:"+uuid);
        item.set("resource",resource);
        ObjectNode request=item.putObject("request");
        request.put("method","POST");
        request.put("url",url);
    }

    /** ID resource dari entry balasan Bundle transaction (resourceID atau location .../Tipe/{id}/_history/...). */
    public static List<String[]> bacaHasilBundle(JsonNode bundle){
        List<String[]> hasil=new ArrayList<>();
        for(JsonNode entry:bundle.path("entry")){
            JsonNode response=entry.path("response");
            String tipe=response.path("resourceType").asText();
            String id=response.path("resourceID").asText();
            String status=response.path("status").asText();
            String lokasi=response.path("location").asText();
            if(!lokasi.equals("")){
                String[] bagian=lokasi.split("/");
                for(int i=0;i<bagian.length-1;i++){
                    if(bagian[i].matches("[A-Z][A-Za-z]+")&&!bagian[i+1].equals("")&&!bagian[i+1].equals("_history")){
                        if(tipe.equals("")){
                            tipe=bagian[i];
                        }
                        if(id.equals("")){
                            id=bagian[i+1];
                        }
                    }
                }
            }
            hasil.add(new String[]{tipe,id,status});
        }
        return hasil;
    }

    /**
     * Langkah 5: ServiceRequest permintaan rujukan ke faskes yang menerima.
     * @param tenaga {kode,nama} SNOMED performerType
     * @param waktuRencana format FHIR
     */
    public ObjectNode bangunServiceRequest(DataPasien data,String jenis,Proses proses,Kandidat tujuan,String[] tenaga,
            String waktuRencana,String keterangan,String noRujukanBpjs,String instruksi,List<DataPendukung> pendukung){
        ObjectNode sr=mapper.createObjectNode();
        sr.put("resourceType","ServiceRequest");
        ArrayNode identitas=sr.putArray("identifier");
        ObjectNode id=identitas.addObject();
        id.put("system","http://sys-ids.kemkes.go.id/servicerequest/"+idOrg);
        id.put("value",idLokal(data.noRawat,"RJK"));
        if(noRujukanBpjs!=null&&!noRujukanBpjs.trim().equals("")){
            id=identitas.addObject();
            id.put("system",SYS_NO_RUJUKAN_PCARE);
            id.put("value",noRujukanBpjs.trim());
        }
        if(data.bpjs()&&!idOrgBpjs.equals("")){
            id=identitas.addObject();
            id.put("system","https://sys-ids.kemkes.go.id/insurance-subscriber/"+idOrgBpjs);
            id.put("value",data.noPeserta.trim());
        }
        sr.putArray("basedOn").addObject().put("reference","CarePlan/"+(tujuan.idCarePlan.equals("")?proses.idCarePlan:tujuan.idCarePlan));
        sr.put("status","active");
        sr.put("intent","original-order");
        sr.put("priority","stat");
        sr.putArray("category").addObject().putArray("coding").addObject()
                .put("system",SYS_SNOMED).put("code","3457005").put("display","Patient referral");
        codeable(sr,"code",SYS_SNOMED,kodeManajemen(jenis),namaManajemen(jenis)).put("text",keterangan);
        referensi(sr,"subject","Patient/"+data.idPasien,null);
        referensi(sr,"encounter","Encounter/"+data.idEncounter,null);
        sr.put("occurrenceDateTime",waktuRencana);
        referensi(sr,"requester","Organization/"+idOrg,namaOrg);
        codeable(sr,"performerType",SYS_SNOMED,tenaga[0],tenaga[1]);
        ObjectNode performer=sr.putArray("performer").addObject();
        performer.put("reference","Organization/"+tujuan.idOrg);
        if(!tujuan.nama.equals("")){
            performer.put("display",tujuan.nama);
        }
        ArrayNode alasan=sr.putArray("reasonReference");
        for(Diagnosa diagnosa:data.diagnosa){
            if(!diagnosa.idCondition.equals("")){
                alasan.addObject().put("reference","Condition/"+diagnosa.idCondition);
            }
        }
        if(alasan.size()==0){
            sr.remove("reasonReference");
        }
        sr.putArray("locationCode").addObject().putArray("coding").addObject()
                .put("system","http://terminology.hl7.org/CodeSystem/v3-RoleCode").put("code","HOSP").put("display","Hospital");
        ArrayNode supporting=sr.putArray("supportingInfo");
        ObjectNode ref=supporting.addObject();
        ref.put("display","Task Respon Kandidat Faskes Rujukan");
        ref.put("reference","Task/"+tujuan.idTask);
        for(DataPendukung item:pendukung){
            if(item.dipilih){
                ref=supporting.addObject();
                ref.put("display",item.keterangan);
                ref.put("reference",item.resource+"/"+item.id);
            }
        }
        if(instruksi!=null&&!instruksi.trim().equals("")){
            sr.put("patientInstruction",instruksi.trim());
        }
        return sr;
    }

    /** JSON Patch status cancelled (pembatalan Task oleh perujuk). */
    public ArrayNode bangunPatchBatal(){
        ArrayNode patch=mapper.createArrayNode();
        patch.addObject().put("op","replace").put("path","/status").put("value","cancelled");
        return patch;
    }

    /** JSON Patch respon faskes rujukan: status completed + output accepted/rejected. */
    public ArrayNode bangunPatchRespon(boolean terima){
        ArrayNode patch=mapper.createArrayNode();
        patch.addObject().put("op","replace").put("path","/status").put("value","completed");
        ObjectNode tambah=patch.addObject();
        tambah.put("op","add");
        tambah.put("path","/output");
        ObjectNode output=tambah.putArray("value").addObject();
        codeable(output,"type",SYS_KEMKES,"response-referral-task","Response referral task").put("text","Respon atas Task Rujukan");
        coding(output,"valueCoding","http://hl7.org/fhir/task-status",terima?"accepted":"rejected",terima?"Accepted":"Rejected");
        return patch;
    }

    /** JSON Patch Encounter.basedOn ServiceRequest rujukan (faskes rujukan). */
    public ArrayNode bangunPatchEncounterRujukan(String idServiceRequest){
        ArrayNode patch=mapper.createArrayNode();
        ObjectNode tambah=patch.addObject();
        tambah.put("op","add");
        tambah.put("path","/basedOn");
        tambah.putArray("value").addObject().put("reference","ServiceRequest/"+idServiceRequest);
        return patch;
    }

    // ===================================================================================
    // Pembantu baca resource
    // ===================================================================================

    public static String kodePertama(JsonNode codeable){
        return codeable.path("coding").path(0).path("code").asText();
    }

    public static String idDariReferensi(String referensi){
        int posisi=referensi.lastIndexOf('/');
        return posisi>=0?referensi.substring(posisi+1):referensi;
    }

    /** Respon faskes tujuan pada Task persetujuan: accepted/rejected/"" */
    public static String bacaRespon(JsonNode task){
        for(JsonNode output:task.path("output")){
            if(kodePertama(output.path("type")).equals("response-referral-task")){
                return output.path("valueCoding").path("code").asText();
            }
        }
        return "";
    }

    /** Identifier dengan system tertentu. */
    public static String bacaIdentifier(JsonNode resource,String system){
        for(JsonNode id:resource.path("identifier")){
            if(id.path("system").asText().equals(system)){
                return id.path("value").asText();
            }
        }
        return "";
    }

    /** Ringkasan satu baris resource (untuk menampilkan supportingInfo rujukan masuk). */
    public static String ringkas(JsonNode resource){
        String tipe=resource.path("resourceType").asText();
        StringBuilder teks=new StringBuilder(tipe);
        JsonNode code=resource.path("code");
        String nama=code.path("text").asText();
        if(nama.equals("")){
            nama=code.path("coding").path(0).path("display").asText();
        }
        if(nama.equals("")&&tipe.equals("MedicationDispense")){
            nama=resource.path("medicationReference").path("display").asText();
        }
        if(!nama.equals("")){
            teks.append(" | ").append(nama);
        }
        String nilai="";
        if(resource.has("valueQuantity")){
            nilai=resource.path("valueQuantity").path("value").asText()+" "+resource.path("valueQuantity").path("unit").asText();
        }else if(resource.has("valueCodeableConcept")){
            nilai=resource.path("valueCodeableConcept").path("text").asText();
            if(nilai.equals("")){
                nilai=resource.path("valueCodeableConcept").path("coding").path(0).path("display").asText();
            }
        }else if(resource.has("valueString")){
            nilai=resource.path("valueString").asText();
        }else if(resource.has("conclusion")){
            nilai=resource.path("conclusion").asText();
        }else if(resource.has("component")){
            StringBuilder komponen=new StringBuilder();
            for(JsonNode item:resource.path("component")){
                if(komponen.length()>0){
                    komponen.append(", ");
                }
                komponen.append(item.path("code").path("coding").path(0).path("display").asText()).append(" ")
                        .append(item.path("valueQuantity").path("value").asText()).append(" ")
                        .append(item.path("valueQuantity").path("unit").asText());
            }
            nilai=komponen.toString();
        }
        if(!nilai.trim().equals("")){
            teks.append(" : ").append(nilai.trim());
        }
        String waktu=resource.path("effectiveDateTime").asText();
        if(waktu.equals("")){
            waktu=resource.path("recordedDate").asText();
        }
        if(!waktu.equals("")){
            teks.append(" (").append(waktu).append(")");
        }
        return teks.toString();
    }

    // ===================================================================================
    // Penyimpanan
    // ===================================================================================

    /** true bila tabel satu_sehat_rujukan sudah dibuat (sik_satu_sehat_rujukan.sql). */
    public boolean tabelTersedia(){
        boolean ada=false;
        try {
            ps=koneksi.prepareStatement("select count(*) from satu_sehat_rujukan_referensi");
            try {
                rs=ps.executeQuery();
                ada=rs.next();
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
                ada=false;
            } finally{
                tutup();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return ada;
    }

    public boolean simpanProses(Proses proses){
        return eksekusi("insert into satu_sehat_rujukan(no_rawat,tgl_rujukan,jenis,kd_dokter,id_task_pra,status) values(?,?,?,?,?,?)",
                proses.noRawat,proses.tglRujukan,proses.jenis,proses.kdDokter,proses.idTaskPra,proses.status);
    }

    public boolean ubahProses(Proses proses){
        return eksekusi("update satu_sehat_rujukan set id_task_pra=?,id_task_kandidat=?,id_task_respon_kandidat=?,id_careplan=?,id_servicerequest=?,"+
                "no_rujukan_satusehat=?,id_org_tujuan=?,nm_org_tujuan=?,status=? where no_rawat=? and tgl_rujukan=?",
                proses.idTaskPra,proses.idTaskKandidat,proses.idTaskResponKandidat,proses.idCarePlan,proses.idServiceRequest,
                proses.noRujukanSatuSehat,proses.idOrgTujuan,proses.nmOrgTujuan,proses.status,proses.noRawat,proses.tglRujukan);
    }

    /** Proses rujukan terakhir milik no_rawat, null bila belum ada. */
    public Proses muatProsesTerakhir(String noRawat){
        Proses proses=null;
        try {
            ps=koneksi.prepareStatement(
                    "select satu_sehat_rujukan.no_rawat,date_format(satu_sehat_rujukan.tgl_rujukan,'%Y-%m-%d %H:%i:%s'),satu_sehat_rujukan.jenis,satu_sehat_rujukan.kd_dokter,"+
                    "satu_sehat_rujukan.id_task_pra,satu_sehat_rujukan.id_task_kandidat,satu_sehat_rujukan.id_task_respon_kandidat,satu_sehat_rujukan.id_careplan,"+
                    "satu_sehat_rujukan.id_servicerequest,satu_sehat_rujukan.no_rujukan_satusehat,satu_sehat_rujukan.id_org_tujuan,satu_sehat_rujukan.nm_org_tujuan,"+
                    "satu_sehat_rujukan.status from satu_sehat_rujukan where satu_sehat_rujukan.no_rawat=? order by satu_sehat_rujukan.tgl_rujukan desc limit 1");
            try {
                ps.setString(1,noRawat);
                rs=ps.executeQuery();
                if(rs.next()){
                    proses=new Proses();
                    proses.noRawat=kosong(rs.getString(1));
                    proses.tglRujukan=kosong(rs.getString(2));
                    proses.jenis=kosong(rs.getString(3));
                    proses.kdDokter=kosong(rs.getString(4));
                    proses.idTaskPra=kosong(rs.getString(5));
                    proses.idTaskKandidat=kosong(rs.getString(6));
                    proses.idTaskResponKandidat=kosong(rs.getString(7));
                    proses.idCarePlan=kosong(rs.getString(8));
                    proses.idServiceRequest=kosong(rs.getString(9));
                    proses.noRujukanSatuSehat=kosong(rs.getString(10));
                    proses.idOrgTujuan=kosong(rs.getString(11));
                    proses.nmOrgTujuan=kosong(rs.getString(12));
                    proses.status=kosong(rs.getString(13));
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                tutup();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return proses;
    }

    public boolean simpanTask(Proses proses,Kandidat kandidat){
        return eksekusi("insert into satu_sehat_rujukan_task(id_task,no_rawat,tgl_rujukan,id_org,nm_org,kode_bpjs,jarak,strata,id_careplan,status,respon) values(?,?,?,?,?,?,?,?,?,?,?)",
                kandidat.idTask,proses.noRawat,proses.tglRujukan,kandidat.idOrg,kandidat.nama,kandidat.kodeBpjs,kandidat.jarak,kandidat.strata,kandidat.idCarePlan,kandidat.status,kandidat.respon);
    }

    public boolean ubahTask(Kandidat kandidat){
        return eksekusi("update satu_sehat_rujukan_task set status=?,respon=? where id_task=?",kandidat.status,kandidat.respon,kandidat.idTask);
    }

    /** Task persetujuan yang sudah terkirim untuk proses rujukan. */
    public List<Kandidat> muatTask(Proses proses){
        List<Kandidat> hasil=new ArrayList<>();
        try {
            ps=koneksi.prepareStatement(
                    "select satu_sehat_rujukan_task.id_task,satu_sehat_rujukan_task.id_org,satu_sehat_rujukan_task.nm_org,satu_sehat_rujukan_task.kode_bpjs,"+
                    "satu_sehat_rujukan_task.jarak,satu_sehat_rujukan_task.strata,satu_sehat_rujukan_task.status,satu_sehat_rujukan_task.respon,satu_sehat_rujukan_task.id_careplan "+
                    "from satu_sehat_rujukan_task where satu_sehat_rujukan_task.no_rawat=? and satu_sehat_rujukan_task.tgl_rujukan=?");
            try {
                ps.setString(1,proses.noRawat);
                ps.setString(2,proses.tglRujukan);
                rs=ps.executeQuery();
                while(rs.next()){
                    Kandidat kandidat=new Kandidat();
                    kandidat.idTask=kosong(rs.getString(1));
                    kandidat.idOrg=kosong(rs.getString(2));
                    kandidat.nama=kosong(rs.getString(3));
                    kandidat.kodeBpjs=kosong(rs.getString(4));
                    kandidat.jarak=kosong(rs.getString(5));
                    kandidat.strata=kosong(rs.getString(6));
                    kandidat.status=kosong(rs.getString(7));
                    kandidat.respon=kosong(rs.getString(8));
                    kandidat.idCarePlan=kosong(rs.getString(9));
                    hasil.add(kandidat);
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                tutup();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return hasil;
    }

    public boolean simpanRujukanMasuk(String idServiceRequest,String noRujukan,String idTask,String idOrgPerujuk,String nmOrgPerujuk,
            String idPasien,String noRawat,String idEncounter){
        return eksekusi("insert into satu_sehat_rujukan_masuk(id_servicerequest,no_rujukan_satusehat,id_task,id_org_perujuk,nm_org_perujuk,id_pasien,no_rawat,id_encounter,tgl_hubung) "+
                "values(?,?,?,?,?,?,?,?,now()) on duplicate key update no_rujukan_satusehat=values(no_rujukan_satusehat),id_task=values(id_task),"+
                "id_org_perujuk=values(id_org_perujuk),nm_org_perujuk=values(nm_org_perujuk),id_pasien=values(id_pasien),no_rawat=values(no_rawat),"+
                "id_encounter=values(id_encounter),tgl_hubung=now()",
                idServiceRequest,noRujukan,idTask,idOrgPerujuk,nmOrgPerujuk,idPasien,noRawat,idEncounter);
    }

    /** Daftar referensi kode dalam bentuk "KODE - Nama". */
    public List<String> muatReferensi(String jenis){
        List<String> hasil=new ArrayList<>();
        try {
            ps=koneksi.prepareStatement("select satu_sehat_rujukan_referensi.kode,satu_sehat_rujukan_referensi.nama from satu_sehat_rujukan_referensi "+
                    "where satu_sehat_rujukan_referensi.jenis=? order by satu_sehat_rujukan_referensi.nama");
            try {
                ps.setString(1,jenis);
                rs=ps.executeQuery();
                while(rs.next()){
                    hasil.add(rs.getString(1)+" - "+rs.getString(2));
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                tutup();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return hasil;
    }

    /** Menyimpan kode baru yang diketik petugas agar muncul lagi di pilihan berikutnya. */
    public void simpanReferensi(String jenis,String[] kodeNama){
        if(kodeNama!=null){
            eksekusi("insert ignore into satu_sehat_rujukan_referensi(jenis,kode,nama) values(?,?,?)",jenis,kodeNama[0],kodeNama[1]);
        }
    }

    /** "KODE - Nama" -> {KODE,Nama}; null bila kosong/format salah. */
    public static String[] pisahKode(Object teks){
        if(teks==null){
            return null;
        }
        String nilai=teks.toString().trim();
        int posisi=nilai.indexOf(" - ");
        if(posisi<=0||posisi+3>=nilai.length()){
            return null;
        }
        return new String[]{nilai.substring(0,posisi).trim(),nilai.substring(posisi+3).trim()};
    }

    /** Nama organisasi SATUSEHAT (dengan cache sederhana). */
    private final Map<String,String> cacheOrganisasi=new LinkedHashMap<>();

    public String namaOrganisasi(String id){
        if(id==null||id.equals("")){
            return "";
        }
        if(cacheOrganisasi.containsKey(id)){
            return cacheOrganisasi.get(id);
        }
        Respon respon=kirim("GET","/Organization/"+id,null);
        String nama=respon.sukses()?respon.json.path("name").asText():"";
        cacheOrganisasi.put(id,nama);
        return nama;
    }

    private boolean eksekusi(String sql,String... nilai){
        boolean sukses=false;
        try {
            ps=koneksi.prepareStatement(sql);
            try {
                for(int i=0;i<nilai.length;i++){
                    ps.setString(i+1,nilai[i]==null||nilai[i].equals("")?null:nilai[i]);
                }
                ps.executeUpdate();
                sukses=true;
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return sukses;
    }

    private String cariIsi(String sql,String... nilai){
        String hasil="";
        try {
            ps=koneksi.prepareStatement(sql);
            try {
                for(int i=0;i<nilai.length;i++){
                    ps.setString(i+1,nilai[i]);
                }
                rs=ps.executeQuery();
                if(rs.next()){
                    hasil=kosong(rs.getString(1));
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                tutup();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return hasil;
    }

    private void tutup(){
        try {
            if(rs!=null){
                rs.close();
            }
            if(ps!=null){
                ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        rs=null;
    }

    private static String kosong(String teks){
        return teks==null?"":teks.trim();
    }
}
