package modif;

import bridging.ApiBPJS;
import bridging.ApiSatuSehat;
import bridging.SatuSehatCekNIK;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Fungsi pendukung Rujukan RS via VClaim yang terintegrasi Satu Sehat (Sisrute).
 * Dokumentasi BPJS: collection Postman "INTEGRASI SATU SEHAT RUJUKAN", folder "RUJUKAN RS via VCLAIM".
 *
 * Urutan pemakaian dari BPJSRujukanKeluar :
 * 1. siapkan(norawat,nosep)                : cari encounter, ID IHS pasien dan dokter, cek kesesuaian datanya
 * 2. ambilKriteria(kddiagnosa)             : ambil kriteria rujukan dan wilayah jejaring dari BPJS
 * 3. pilihKriteria(parent)                 : petugas menjawab kriteria dan memilih wilayah
 * 4. pilihFaskes(parent,diag,poli,tgl)     : cari dan pilih RS tujuan
 * 5. jsonInsert()                          : blok "satuSehatRujukan" untuk /Rujukan/Insert
 * 6. simpan(norujukan,responserujukan)     : simpan data Satu Sehat setelah insert berhasil
 * 7. jsonHapus(norujukan) dan hapus(...)   : untuk /Rujukan/Delete
 *
 * Data disimpan di tabel bridging_rujukan_bpjs_satusehat, bukan kolom baru di bridging_rujukan_bpjs,
 * karena insert bridging_rujukan_bpjs (BPJSRujukanKeluar dan BPJSDataSEP) tanpa daftar kolom.
 */
public class RujukanSatuSehatVClaim {
    /**
     * Path endpoint, mengikuti dokumentasi. Base URL berbeda dari VClaim biasa: service vclaim-sisrute-rest,
     * diisi di setting/database.xml kunci URLAPIBPJSSISRUTE
     * (development: https://apijkn-dev.bpjs-kesehatan.go.id/vclaim-sisrute-rest/api/v1.0, info BPJS 13-11-2026).
     */
    public static final String URL_KRITERIA="/Rujukan/GetKriteriaRujukan";
    public static final String URL_FASKES="/Rujukan/GetFaskesRujukan";
    public static final String URL_INSERT="/Rujukan/Insert";
    public static final String URL_HAPUS="/Rujukan/Delete";
    public static final String URL_UPDATE="/Rujukan/Update";
    /** Referensi kelompok layanan (update katalog BPJS 18-11-2026, skema RS dengan kompetensi kelompok layanan di atasnya). */
    public static final String URL_KELOMPOK_LAYANAN="/Referensi/kelompoklayanan";
    public static final String URL_KELOMPOK_LAYANAN_DIAGNOSA="/Referensi/kelompoklayanan/diagnosa/";
    /** Nama field wajib baru di GetFaskesRujukan, sesuai pengumuman BPJS 18-11-2026 (development belum mewajibkan, belum bisa diuji). */
    public static final String FIELD_KELOMPOK_LAYANAN="IdKelompokLayanan";
    private static final String TABEL="bridging_rujukan_bpjs_satusehat";

    private final Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private final ApiBPJS api=new ApiBPJS();
    private final ObjectMapper mapper=new ObjectMapper();
    private String link="",pesan="";

    private String norawat="",nokartu="",idencounter="",idpasien="",iddokter="",kdfaskesasal="",
                   patientinstruction="",keteranganrujukan="",kddiagnosakriteria="",linkfhir="",
                   idkriteriarujukan="",idfaskesrujukan="",kdppkperujuk="";
    private final List<Kriteria> daftarkriteria=new ArrayList<>();
    private final List<Wilayah> daftarwilayah=new ArrayList<>();
    private final List<Wilayah> daftarkabupaten=new ArrayList<>();
    private final List<KelompokLayanan> daftarkelompoklayanan=new ArrayList<>();
    private Wilayah wilayah=null;
    private KelompokLayanan kelompoklayanan=null;
    private Faskes faskestujuan=null;

    /**
     * Satu kriteria rujukan dari GetKriteriaRujukan beserta jawabannya.
     * Kriteria ya/tidak dikirim sebagai valueBoolean=dipilih. Kriteria Tindakan Medis (type text)
     * dikirim sebagai valueString=kode ICD-9 bila dipilih, string kosong bila tidak dipilih.
     */
    public static class Kriteria {
        public String linkid="",text="",type="";
        public boolean dipilih=false;
        public String kodetindakan="",namatindakan="";

        /** true bila jawaban berupa kode tindakan ICD-9 (valueString), false bila ya/tidak (valueBoolean). */
        public boolean isText(){
            return type.equalsIgnoreCase("text");
        }
    }

    /** Wilayah jejaring rujukan (provinsi, kabupaten opsional). */
    public static class Wilayah {
        public String kodepropinsi="",namapropinsi="",kodekabupaten="",namakabupaten="";

        @Override
        public String toString(){
            if(!kodekabupaten.equals("")){
                return kodekabupaten+" - "+namakabupaten;
            }else if(kodepropinsi.equals("")){
                return "- Semua Kabupaten/Kota -";
            }
            return kodepropinsi+" - "+namapropinsi;
        }
    }

    /** Kelompok layanan dari /Referensi/kelompoklayanan, dikirim sebagai IdKelompokLayanan di GetFaskesRujukan. */
    public static class KelompokLayanan {
        public int id=0;
        public String nama="";

        @Override
        public String toString(){
            return id+" - "+nama;
        }
    }

    /** Satu faskes tujuan dari GetFaskesRujukan. */
    public static class Faskes {
        public String kodesatusehat="",kdppk="",nmppk="",strata="",alamat="",telp="",kelas="",nmkc="",jadwal="";
        public double jarak=0,persentase=0;
        public int jmlrujuk=0,kapasitas=0;
    }

    public RujukanSatuSehatVClaim(){
        try {
            link=bacaSetting("URLAPIBPJSSISRUTE");
            // Kode faskes asal untuk SATUSEHAT Rujukan harus kode faskes resmi (angka, contoh 100027474).
            // Di development, kredensial staging terikat ke Organization ID lain (UUID) yang dipakai Encounter,
            // jadi kode resmi diisi terpisah di IDSATUSEHATRUJUKAN. Production cukup dikosongkan: pakai IDSATUSEHAT.
            kdfaskesasal=bacaSetting("IDSATUSEHATRUJUKAN");
            if(kdfaskesasal.equals("")){
                kdfaskesasal=koneksiDB.IDSATUSEHAT();
            }
            linkfhir=koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    /** Base URL vclaim-sisrute-rest, dipakai BPJSRujukanKeluar untuk Insert dan Delete rujukan Satu Sehat. */
    public String getLink(){
        return link;
    }

    /** Membaca satu kunci dari setting/database.xml, kosong bila tidak ada. */
    private static String bacaSetting(String kunci){
        String hasil="";
        try {
            java.util.Properties prop=new java.util.Properties();
            prop.loadFromXML(new java.io.FileInputStream("setting/database.xml"));
            hasil=prop.getProperty(kunci,"").trim();
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return hasil;
    }

    /**
     * Penerima log request/response API (permintaan BPJS saat UAT), ditampilkan di panel kanan BPJSRujukanKeluar.
     * user_key dan secret tidak ikut dicatat, hanya method, URL, body request dan response.
     */
    public interface PencatatApi {
        void catat(String judul,String metode,String url,String request,String response);
    }

    private PencatatApi pencatat;

    public void setPencatat(PencatatApi pencatat){
        this.pencatat=pencatat;
    }

    /** true bila TAMPILLOGAPIRUJUKAN=yes di setting/database.xml. Hanya untuk UAT, production tidak perlu diisi. */
    public static boolean isLogApiAktif(){
        return bacaSetting("TAMPILLOGAPIRUJUKAN").equalsIgnoreCase("yes");
    }

    /** Mencatat hit BPJS. Response terenkripsi ditampilkan sudah didekripsi agar mudah dibaca. */
    public void catatBpjs(String judul,String metode,String url,String request,String mentah,String utc){
        if(pencatat==null){
            return;
        }
        String isi=mentah;
        try {
            JsonNode root=mapper.readTree(mentah);
            ObjectNode tampil=mapper.createObjectNode();
            tampil.set("metaData",root.path("metaData"));
            JsonNode response=root.path("response");
            if(response.isTextual()&&!response.asText().equals("")){
                try {
                    response=mapper.readTree(api.Decrypt(response.asText(),utc));
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
            if(!response.isMissingNode()){
                tampil.set("response",response);
            }
            isi=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tampil);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        pencatat.catat(judul,metode,url,rapikan(request),isi);
    }

    /** Mencatat hit yang gagal (HTTP 4xx/5xx, timeout, dsb), isi body error ikut ditampilkan bila ada. */
    public void catatError(String judul,String metode,String url,String request,Exception e){
        if(pencatat==null){
            return;
        }
        String isi=e.toString();
        if(e instanceof HttpStatusCodeException){
            isi=((HttpStatusCodeException)e).getStatusCode()+"\n"+rapikan(teks(((HttpStatusCodeException)e).getResponseBodyAsByteArray()));
        }
        pencatat.catat(judul+" (GAGAL)",metode,url,rapikan(request),isi);
    }

    /** JSON dirapikan (pretty print), teks selain JSON dikembalikan apa adanya. */
    private String rapikan(String json){
        if(json==null||json.trim().equals("")){
            return "";
        }
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(json));
        } catch (Exception e) {
            return json;
        }
    }

    /** Mengosongkan semua data kunjungan, kriteria dan faskes. Dipanggil saat form dikosongkan. */
    public void reset(){
        norawat="";nokartu="";idencounter="";idpasien="";iddokter="";pesan="";kddiagnosakriteria="";
        patientinstruction="";keteranganrujukan="";idkriteriarujukan="";idfaskesrujukan="";kdppkperujuk="";
        daftarkriteria.clear();
        daftarwilayah.clear();
        daftarkabupaten.clear();
        daftarkelompoklayanan.clear();
        wilayah=null;
        kelompoklayanan=null;
        faskestujuan=null;
    }

    /** Pesan kesalahan terakhir, untuk ditampilkan ke petugas. */
    public String getPesan(){
        return pesan;
    }

    /**
     * Mengambil data Satu Sehat kunjungan : encounter, ID IHS pasien (NIK pasien) dan
     * ID IHS dokter (NIK dokter reg_periksa). Pilihan kriteria dan faskes sebelumnya dikosongkan.
     * Sekaligus memastikan no kartu BPJS, ID IHS pasien dan encounter berasal dari pasien yang sama :
     * - SEP milik no rawat yang sama dan no kartu SEP sama dengan no peserta pasien
     * - encounter di server Satu Sehat (GET Encounter/{id}) subject-nya Patient/{ID IHS pasien}
     * @param norawat no rawat kunjungan yang dirujuk
     * @param nosep   no SEP kunjungan yang dirujuk
     * @return false bila ada data yang tidak ditemukan atau tidak sesuai, alasannya ada di getPesan()
     */
    public boolean siapkan(String norawat,String nosep){
        reset();
        this.norawat=norawat;
        if(link.equals("")){
            pesan="URL API BPJS Sisrute (URLAPIBPJSSISRUTE) belum diisi di setting/database.xml";
            return false;
        }
        String nikpasien="",nikdokter="",nopeserta="",norawatsep="";
        boolean adasep=false;
        try {
            ps=koneksi.prepareStatement(
                "select ifnull(satu_sehat_encounter.id_encounter,''),ifnull(pasien.no_ktp,''),ifnull(pegawai.no_ktp,''),"+
                "ifnull(pasien.no_peserta,'') from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "left join pegawai on reg_periksa.kd_dokter=pegawai.nik "+
                "left join satu_sehat_encounter on reg_periksa.no_rawat=satu_sehat_encounter.no_rawat "+
                "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,norawat);
                rs=ps.executeQuery();
                if(rs.next()){
                    idencounter=rs.getString(1).trim();
                    nikpasien=rs.getString(2).trim();
                    nikdokter=rs.getString(3).trim();
                    nopeserta=rs.getString(4).trim();
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

            ps=koneksi.prepareStatement("select no_rawat,no_kartu,kdppkpelayanan from bridging_sep where no_sep=?");
            try {
                ps.setString(1,nosep);
                rs=ps.executeQuery();
                if(rs.next()){
                    adasep=true;
                    norawatsep=rs.getString(1).trim();
                    nokartu=rs.getString(2).trim();
                    kdppkperujuk=rs.getString(3).trim();
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

        if(kdfaskesasal.equals("")){
            pesan="ID organisasi Satu Sehat RS belum diatur di setting";
        }else if(!adasep){
            pesan="SEP "+nosep+" tidak ditemukan";
        }else if(!norawatsep.equals(norawat)){
            pesan="Data tidak sesuai : SEP "+nosep+" milik no rawat "+norawatsep+", bukan "+norawat;
        }else if(!nokartu.equals(nopeserta)){
            pesan="Data tidak sesuai : No. Kartu BPJS di SEP ("+nokartu+") berbeda dengan No. Peserta pasien ("+nopeserta+")";
        }else if(idencounter.equals("")){
            pesan="Encounter kunjungan "+norawat+" belum dikirim ke Satu Sehat.\nKirim encounter dulu sebelum membuat rujukan.";
        }else if(nikpasien.equals("")){
            pesan="NIK pasien masih kosong";
        }else if(nikdokter.equals("")){
            pesan="NIK dokter masih kosong";
        }else{
            SatuSehatCekNIK ceksatusehat=new SatuSehatCekNIK();
            idpasien=ceksatusehat.tampilIDPasien(nikpasien);
            if(idpasien.equals("")){
                pesan="ID IHS pasien tidak ditemukan di Satu Sehat (NIK "+nikpasien+")";
            }else{
                iddokter=ceksatusehat.tampilIDParktisi(nikdokter);
                if(iddokter.equals("")){
                    pesan="ID IHS dokter tidak ditemukan di Satu Sehat (NIK "+nikdokter+")";
                }else{
                    String pasienencounter=cariPasienEncounter(idencounter);
                    if(pasienencounter.equals("")){
                        pesan="Encounter "+idencounter+" tidak ditemukan di server Satu Sehat";
                    }else if(!pasienencounter.equals("Patient/"+idpasien)){
                        pesan="Data tidak sesuai : Encounter "+idencounter+" milik "+pasienencounter+
                              ", bukan pasien ini (Patient/"+idpasien+")";
                    }
                }
            }
        }
        if(!pesan.equals("")){
            // data tidak lengkap/tidak sesuai, rujukan Satu Sehat tidak boleh dilanjutkan
            idencounter="";idpasien="";iddokter="";
        }
        return pesan.equals("");
    }

    /** Ringkasan data kunjungan yang dipakai, ditampilkan di jendela kriteria sebagai bukti kesesuaian data. */
    public String getRingkasanData(){
        return "No. Kartu BPJS : "+nokartu+"   |   ID Pasien Satu Sehat : "+idpasien+"   |   Encounter : "+idencounter;
    }

    /** Mengambil subject.reference encounter dari server Satu Sehat, kosong bila tidak ditemukan/gagal. */
    private String cariPasienEncounter(String encounter){
        String hasil="";
        try {
            ApiSatuSehat apisatusehat=new ApiSatuSehat();
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization","Bearer "+apisatusehat.TokenSatuSehat());
            System.out.println("URL : "+linkfhir+"/Encounter/"+encounter);
            String json=apisatusehat.getRest().exchange(linkfhir+"/Encounter/"+encounter,HttpMethod.GET,new HttpEntity(headers),String.class).getBody();
            System.out.println("JSON : "+json);
            if(pencatat!=null){
                pencatat.catat("Satu Sehat Cek Encounter","GET",linkfhir+"/Encounter/"+encounter,"",rapikan(json));
            }
            hasil=mapper.readTree(json).path("subject").path("reference").asText();
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            catatError("Satu Sehat Cek Encounter","GET",linkfhir+"/Encounter/"+encounter,"",e);
        }
        return hasil;
    }

    /**
     * Mengambil daftar kriteria rujukan dan wilayah jejaring dari BPJS. Jawaban sebelumnya dikosongkan.
     * @param kddiagnosa kode ICD-10 diagnosa rujukan
     */
    public boolean ambilKriteria(String kddiagnosa){
        daftarkriteria.clear();
        daftarwilayah.clear();
        daftarkabupaten.clear();
        daftarkelompoklayanan.clear();
        wilayah=null;
        kelompoklayanan=null;
        faskestujuan=null;
        idkriteriarujukan="";
        idfaskesrujukan="";
        kddiagnosakriteria=kddiagnosa;
        pesan="";
        if(idencounter.equals("")){
            pesan="Data Satu Sehat belum disiapkan";
            return false;
        }
        try {
            ObjectNode body=mapper.createObjectNode();
            body.put("kodeFaskesSatuSehat",kdfaskesasal);
            body.put("kodeDiagnosa",kddiagnosa);
            // GetKriteriaRujukan dan GetFaskesRujukan memakai format "Encounter/<id>"
            body.putObject("encounter").put("reference","Encounter/"+idencounter);
            JsonNode response=kirimBpjs(URL_KRITERIA,mapper.writeValueAsString(body));
            if(response!=null){
                // wajib dikirim ulang saat Insert (update katalog BPJS 18-11-2026)
                idkriteriarujukan=response.path("idKriteriaRujukan").asText();
                for(JsonNode list:response.path("kriteriaRujukan")){
                    Kriteria kriteria=new Kriteria();
                    kriteria.linkid=list.path("linkId").asText();
                    kriteria.text=list.path("text").asText();
                    kriteria.type=list.path("type").asText();
                    daftarkriteria.add(kriteria);
                }
                bacaWilayah(response);
                if(daftarkriteria.isEmpty()){
                    pesan="BPJS tidak mengirim kriteria rujukan untuk diagnosa "+kddiagnosa;
                }
            }
            if(pesan.equals("")&&!daftarkriteria.isEmpty()){
                ambilKelompokLayanan(kddiagnosa);
            }
        } catch (Exception e) {
            pesan=pesanKoneksi(e);
        }
        return pesan.equals("")&&!daftarkriteria.isEmpty()&&!daftarkelompoklayanan.isEmpty();
    }

    /**
     * Kelompok layanan sesuai diagnosa (/Referensi/kelompoklayanan/diagnosa/{kd}).
     * Bila BPJS tidak punya pemetaan untuk diagnosa tersebut, dipakai daftar lengkap /Referensi/kelompoklayanan.
     */
    private void ambilKelompokLayanan(String kddiagnosa) throws Exception{
        JsonNode response=kirimBpjs(HttpMethod.GET,URL_KELOMPOK_LAYANAN_DIAGNOSA+kddiagnosa,"");
        if((response==null)||(response.path("list").size()==0)){
            pesan="";
            response=kirimBpjs(HttpMethod.GET,URL_KELOMPOK_LAYANAN,"");
        }
        if(response!=null){
            for(JsonNode list:response.path("list")){
                KelompokLayanan baru=new KelompokLayanan();
                baru.id=list.path("id").asInt();
                baru.nama=list.path("nama").asText();
                daftarkelompoklayanan.add(baru);
            }
        }
        if(daftarkelompoklayanan.isEmpty()&&pesan.equals("")){
            pesan="BPJS tidak mengirim referensi kelompok layanan";
        }
    }

    /**
     * Membaca wilayah jejaring dari response GetKriteriaRujukan.
     * Development (13-11-2026) mengirim bentuk Questionnaire: "JejaringWilayah"[0].item, linkId 1.1 Provinsi dan
     * 1.2 Kabupaten/Kota, masing-masing answerOption[].valueCoding{code,display}.
     * Bentuk di dokumentasi ("jejaringWilayahRujukan":[{code,wilayah}]) tetap dibaca.
     */
    private void bacaWilayah(JsonNode response){
        for(JsonNode grup:response.path("JejaringWilayah")){
            for(JsonNode item:grup.path("item")){
                boolean propinsi=item.path("text").asText().toLowerCase().contains("provinsi");
                for(JsonNode opsi:item.path("answerOption")){
                    Wilayah baru=new Wilayah();
                    if(propinsi){
                        baru.kodepropinsi=opsi.path("valueCoding").path("code").asText();
                        baru.namapropinsi=opsi.path("valueCoding").path("display").asText();
                        daftarwilayah.add(baru);
                    }else{
                        baru.kodekabupaten=opsi.path("valueCoding").path("code").asText();
                        baru.namakabupaten=opsi.path("valueCoding").path("display").asText();
                        daftarkabupaten.add(baru);
                    }
                }
            }
        }
        for(JsonNode list:response.path("jejaringWilayahRujukan")){
            Wilayah baru=new Wilayah();
            baru.kodepropinsi=list.path("code").asText();
            baru.namapropinsi=list.path("wilayah").asText();
            daftarwilayah.add(baru);
        }
    }

    /**
     * Mencari faskes tujuan dari BPJS sesuai diagnosa, spesialis, kriteria dan wilayah.
     * @param kdspesialis      kode poli/spesialis VClaim yang dituju
     * @param tglrencana       tanggal rencana kunjungan, format yyyy-MM-dd (sudah dikonfirmasi)
     * @return daftar faskes, kosong bila gagal (alasan di getPesan())
     */
    public List<Faskes> cariFaskes(String kddiagnosa,String kdspesialis,String tglrencana){
        List<Faskes> hasil=new ArrayList<>();
        pesan="";
        idfaskesrujukan="";
        if(!kriteriaLengkap()){
            pesan="Kriteria rujukan, kelompok layanan dan wilayah jejaring belum diisi";
            return hasil;
        }
        try {
            ObjectNode body=mapper.createObjectNode();
            body.put("kodeFaskesSatuSehat",kdfaskesasal);
            body.put("kodeDiagnosa",kddiagnosa);
            body.put("kodeSpesialis",kdspesialis);
            body.put("tglRencanaKunjungan",tglrencana);
            body.put(FIELD_KELOMPOK_LAYANAN,kelompoklayanan.id);
            body.set("kriteriaRujukan",nodeKriteria());
            body.set("codeJejaringWilayah",nodeWilayah());
            body.putObject("encounter").put("reference","Encounter/"+idencounter);
            JsonNode response=kirimBpjs(URL_FASKES,mapper.writeValueAsString(body));
            if(response!=null){
                // wajib dikirim ulang saat Insert (update katalog BPJS 18-11-2026)
                idfaskesrujukan=response.path("idFaskesRujukan").asText();
                for(JsonNode list:response.path("list")){
                    Faskes faskes=new Faskes();
                    // development mengirim "Organization/100026146", insert memakai angkanya saja
                    faskes.kodesatusehat=list.path("kodeFaskesSatuSehat").asText().replace("Organization/","");
                    // katalog BPJS 18-11-2026: faskes tanpa kode Satu Sehat dikirim "Organization/null"
                    if(faskes.kodesatusehat.equals("null")){
                        faskes.kodesatusehat="";
                    }
                    faskes.kdppk=list.path("kdppk").asText();
                    faskes.nmppk=list.path("nmppk").asText();
                    faskes.strata=list.path("strataSatuSehat").asText();
                    faskes.alamat=list.path("alamatPpk").asText();
                    faskes.telp=list.path("telpPpk").asText();
                    faskes.kelas=list.path("kelas").asText();
                    faskes.nmkc=list.path("nmkc").asText();
                    faskes.jadwal=list.path("jadwal").asText();
                    faskes.jarak=list.path("distance").asDouble();
                    faskes.jmlrujuk=list.path("jmlRujuk").asInt();
                    faskes.kapasitas=list.path("kapasitas").asInt();
                    faskes.persentase=list.path("persentase").asDouble();
                    hasil.add(faskes);
                }
                if(hasil.isEmpty()){
                    pesan="Tidak ada faskes tujuan yang sesuai";
                }
            }
        } catch (Exception e) {
            pesan=pesanKoneksi(e);
            // development 13-11-2026 jam 21:00: tglRencanaKunjungan hari ini ditolak hanya dengan "Response Satu Sehat: Task/<id>",
            // besoknya berhasil (kemungkinan jadwal layanan faskes hari itu sudah lewat)
            if(pesan.contains(TANPA_KETERANGAN)){
                pesan=pesan+"\n\nKemungkinan tidak ada jadwal layanan faskes pada tanggal rencana kunjungan "+tglrencana+",\n"+
                      "coba tanggal rencana kunjungan berikutnya.";
            }
        }
        return hasil;
    }

    /** Faskes tujuan yang terakhir dipilih, null bila belum ada. */
    public Faskes getFaskesTujuan(){
        return faskestujuan;
    }

    /** Faskes tujuan yang dipilih di blok Faskes Tujuan (PanelRujukanSatuSehat), null bila belum/dibatalkan. */
    public void setFaskesTujuan(Faskes faskes){
        faskestujuan=faskes;
    }

    /** Wilayah jejaring yang dipilih di blok Kriteria Rujukan. */
    public void setWilayah(Wilayah wilayah){
        this.wilayah=wilayah;
    }

    /** Kelompok layanan yang dipilih di blok Kriteria Rujukan, dikirim sebagai IdKelompokLayanan. */
    public void setKelompokLayanan(KelompokLayanan kelompoklayanan){
        this.kelompoklayanan=kelompoklayanan;
    }

    /** Daftar kriteria hasil GetKriteriaRujukan. Objek yang sama dipakai panel, isian dipilih/kode tindakan diubah langsung. */
    public List<Kriteria> getDaftarKriteria(){
        return daftarkriteria;
    }

    public List<Wilayah> getDaftarWilayah(){
        return daftarwilayah;
    }

    public List<Wilayah> getDaftarKabupaten(){
        return daftarkabupaten;
    }

    public List<KelompokLayanan> getDaftarKelompokLayanan(){
        return daftarkelompoklayanan;
    }

    public String getNoKartu(){
        return nokartu;
    }

    public String getIdPasien(){
        return idpasien;
    }

    public String getIdEncounter(){
        return idencounter;
    }

    /** Instruksi untuk pasien dan keterangan rujukan (opsional). */
    public void setKeterangan(String patientinstruction,String keteranganrujukan){
        this.patientinstruction=patientinstruction==null?"":patientinstruction.trim();
        this.keteranganrujukan=keteranganrujukan==null?"":keteranganrujukan.trim();
    }

    /**
     * Mengecek kesiapan sebelum /Rujukan/Insert. Pesan ada di getPesan().
     * @param kdppkdirujuk kode PPK tujuan di form, harus sama dengan faskes yang dipilih dari daftar Satu Sehat
     * @param kddiagnosa   kode diagnosa di form, harus sama dengan diagnosa saat kriteria diambil
     */
    public boolean siapSimpan(String norawat,String kdppkdirujuk,String kddiagnosa){
        pesan="";
        if(!this.norawat.equals(norawat)||idencounter.equals("")||idpasien.equals("")||iddokter.equals("")){
            pesan="Data Satu Sehat kunjungan belum lengkap, pilih PPK rujukan dulu";
        }else if(!kddiagnosakriteria.equals(kddiagnosa)){
            pesan="Diagnosa berubah setelah kriteria rujukan diisi, pilih ulang PPK rujukan";
        }else if(!kriteriaLengkap()){
            pesan="Kriteria rujukan dan wilayah jejaring belum diisi";
        }else if(faskestujuan==null){
            pesan="Faskes tujuan belum dipilih dari daftar faskes Satu Sehat";
        }else if(!faskestujuan.kdppk.equals(kdppkdirujuk)){
            pesan="PPK tujuan berbeda dengan faskes yang dipilih dari daftar Satu Sehat, pilih ulang faskes tujuan";
        }else if(faskestujuan.kodesatusehat.equals("")){
            pesan="Faskes "+faskestujuan.nmppk+" belum punya kode Satu Sehat, pilih faskes tujuan lain";
        }else if(idkriteriarujukan.equals("")||idfaskesrujukan.equals("")){
            pesan="ID kriteria/faskes rujukan dari BPJS kosong, pilih ulang PPK rujukan";
        }
        return pesan.equals("");
    }

    /**
     * Blok "satuSehatRujukan" untuk body /Rujukan/Insert, ditaruh di dalam "t_rujukan".
     * Contoh : ...+"\"user\": \""+user+"\","+"\"satuSehatRujukan\": "+rujukanss.jsonInsert()+...
     */
    public String jsonInsert(){
        try {
            ObjectNode node=nodeSatuSehat(idencounter,idpasien,iddokter,faskestujuan==null?"":faskestujuan.kodesatusehat,
                                          patientinstruction,keteranganrujukan);
            node.set("kriteriaRujukan",nodeKriteria());
            node.put("keteranganRujukan",keteranganrujukan);
            node.set("codeJejaringWilayah",nodeWilayah());
            // wajib sejak update katalog BPJS 18-11-2026
            node.put("idKriteriaRujukan",idkriteriarujukan);
            node.put("idFaskesRujukan",idfaskesrujukan);
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            return "{}";
        }
    }

    /**
     * Blok "satuSehatRujukan" untuk body PUT /Rujukan/Update (katalog BPJS 18-11-2026): sama dengan Insert
     * ditambah kodeFaskesPerujukBPJS (PPK pelayanan SEP) dan kodeFaskesRujukanBPJS (PPK tujuan).
     * Kriteria dan faskes tujuan harus dipilih ulang lewat blok 3-5 sebelum Update.
     */
    public String jsonUpdate(){
        try {
            ObjectNode node=mapper.createObjectNode();
            node.put("kodeFaskesSatuSehat",kdfaskesasal);
            node.put("kodeFaskesPerujukBPJS",kdppkperujuk);
            node.put("idPasienSatuSehat",nilai(idpasien));
            node.put("kdppkSatuSehatTujuanRujukan",faskestujuan==null?"":faskestujuan.kodesatusehat);
            node.put("kodeFaskesRujukanBPJS",faskestujuan==null?"":faskestujuan.kdppk);
            node.put("kdDokterSatuSehat",nilai(iddokter));
            node.put("patientInstruction",nilai(patientinstruction));
            node.putObject("encounter").put("reference",nilai(idencounter));
            node.set("kriteriaRujukan",nodeKriteria());
            node.put("keteranganRujukan",keteranganrujukan);
            node.set("codeJejaringWilayah",nodeWilayah());
            node.put("idKriteriaRujukan",idkriteriarujukan);
            node.put("idFaskesRujukan",idfaskesrujukan);
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            return "{}";
        }
    }

    /**
     * Memperbarui data Satu Sehat setelah PUT /Rujukan/Update berhasil. Bila rujukan belum punya data Satu Sehat
     * (dibuat sebelum integrasi), data disimpan baru. No. rujukan Satu Sehat dan ServiceRequest lama dipertahankan
     * bila response update tidak mengirimkannya.
     */
    public boolean simpanUpdate(String norujukan,JsonNode responserujukan){
        boolean ada=false;
        try {
            ps=koneksi.prepareStatement("select no_rujukan from "+TABEL+" where no_rujukan=?");
            try {
                ps.setString(1,norujukan);
                rs=ps.executeQuery();
                ada=rs.next();
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
        if(!ada){
            return simpan(norujukan,responserujukan);
        }
        boolean sukses=false;
        try {
            ps=koneksi.prepareStatement(
                "update "+TABEL+" set no_rawat=?,id_encounter=?,id_pasien_ihs=?,id_dokter_ihs=?,kd_faskes_ss_asal=?,"+
                "kd_faskes_ss_tujuan=?,kriteria_rujukan=?,kd_propinsi=?,nm_propinsi=?,kd_kabupaten=?,nm_kabupaten=?,"+
                "patient_instruction=?,keterangan_rujukan=?,no_rujukan_satusehat=if(?='',no_rujukan_satusehat,?),"+
                "service_request_id=if(?='',service_request_id,?) where no_rujukan=?");
            try {
                String nosatusehat=responserujukan.path("noRujukanSatuSehat").asText();
                String servicerequest=responserujukan.path("serviceRequestId").asText();
                ps.setString(1,norawat);
                ps.setString(2,idencounter);
                ps.setString(3,idpasien);
                ps.setString(4,iddokter);
                ps.setString(5,kdfaskesasal);
                ps.setString(6,faskestujuan==null?"":faskestujuan.kodesatusehat);
                ps.setString(7,mapper.writeValueAsString(nodeKriteria()));
                ps.setString(8,wilayah==null?"":wilayah.kodepropinsi);
                ps.setString(9,wilayah==null?"":wilayah.namapropinsi);
                ps.setString(10,wilayah==null?"":wilayah.kodekabupaten);
                ps.setString(11,wilayah==null?"":wilayah.namakabupaten);
                ps.setString(12,patientinstruction);
                ps.setString(13,keteranganrujukan);
                ps.setString(14,nosatusehat);
                ps.setString(15,nosatusehat);
                ps.setString(16,servicerequest);
                ps.setString(17,servicerequest);
                ps.setString(18,norujukan);
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
        if(!sukses){
            JOptionPane.showMessageDialog(null,"Rujukan "+norujukan+" sudah diubah di BPJS, tetapi data Satu Sehat gagal diperbarui di SIMRS.\nHubungi IT.");
        }
        return sukses;
    }

    /**
     * Menyimpan data Satu Sehat setelah /Rujukan/Insert berhasil.
     * @param norujukan       noRujukan dari response
     * @param responserujukan node response.rujukan (berisi noRujukanSatuSehat dan serviceRequestId)
     */
    public boolean simpan(String norujukan,JsonNode responserujukan){
        boolean sukses=false;
        try {
            ps=koneksi.prepareStatement(
                "insert into "+TABEL+"(no_rujukan,no_rawat,id_encounter,id_pasien_ihs,id_dokter_ihs,kd_faskes_ss_asal,"+
                "kd_faskes_ss_tujuan,kriteria_rujukan,kd_propinsi,nm_propinsi,kd_kabupaten,nm_kabupaten,"+
                "patient_instruction,keterangan_rujukan,no_rujukan_satusehat,service_request_id) "+
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            try {
                ps.setString(1,norujukan);
                ps.setString(2,norawat);
                ps.setString(3,idencounter);
                ps.setString(4,idpasien);
                ps.setString(5,iddokter);
                ps.setString(6,kdfaskesasal);
                ps.setString(7,faskestujuan==null?"":faskestujuan.kodesatusehat);
                ps.setString(8,mapper.writeValueAsString(nodeKriteria()));
                ps.setString(9,wilayah==null?"":wilayah.kodepropinsi);
                ps.setString(10,wilayah==null?"":wilayah.namapropinsi);
                ps.setString(11,wilayah==null?"":wilayah.kodekabupaten);
                ps.setString(12,wilayah==null?"":wilayah.namakabupaten);
                ps.setString(13,patientinstruction);
                ps.setString(14,keteranganrujukan);
                ps.setString(15,responserujukan.path("noRujukanSatuSehat").asText());
                ps.setString(16,responserujukan.path("serviceRequestId").asText());
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
        if(!sukses){
            JOptionPane.showMessageDialog(null,"Rujukan "+norujukan+" sudah terkirim ke BPJS, tetapi data Satu Sehat gagal disimpan di SIMRS.\nHubungi IT.");
        }
        return sukses;
    }

    /**
     * Blok "satuSehatRujukan" untuk body /Rujukan/Delete, diambil dari data yang tersimpan.
     * @return null bila rujukan tidak punya data Satu Sehat (rujukan lama), kirim body delete tanpa blok ini
     */
    public String jsonHapus(String norujukan){
        String hasil=null;
        try {
            ps=koneksi.prepareStatement(
                "select id_encounter,id_pasien_ihs,id_dokter_ihs,kd_faskes_ss_asal,kd_faskes_ss_tujuan,"+
                "patient_instruction,keterangan_rujukan from "+TABEL+" where no_rujukan=?");
            try {
                ps.setString(1,norujukan);
                rs=ps.executeQuery();
                if(rs.next()){
                    ObjectNode node=nodeSatuSehat(rs.getString("id_encounter"),rs.getString("id_pasien_ihs"),
                        rs.getString("id_dokter_ihs"),rs.getString("kd_faskes_ss_tujuan"),
                        rs.getString("patient_instruction"),rs.getString("keterangan_rujukan"));
                    node.put("kodeFaskesSatuSehat",rs.getString("kd_faskes_ss_asal"));
                    node.put("keteranganRujukan",nilai(rs.getString("keterangan_rujukan")));
                    hasil=mapper.writeValueAsString(node);
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
        return hasil;
    }

    /** Menghapus data Satu Sehat setelah /Rujukan/Delete berhasil. */
    public void hapus(String norujukan){
        try {
            ps=koneksi.prepareStatement("delete from "+TABEL+" where no_rujukan=?");
            try {
                ps.setString(1,norujukan);
                ps.executeUpdate();
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
    }

    /**
     * Membaca isi "response" BPJS. VClaim biasanya mengirim response terenkripsi (teks),
     * bila ternyata berupa objek JSON langsung dipakai.
     */
    public JsonNode bacaResponse(JsonNode root,String utc) throws Exception{
        JsonNode response=root.path("response");
        if(response.isTextual()){
            return mapper.readTree(api.Decrypt(response.asText(),utc));
        }
        return response;
    }

    private boolean kriteriaLengkap(){
        return !daftarkriteria.isEmpty()&&(wilayah!=null)&&(kelompoklayanan!=null);
    }

    /** POST ke BPJS dengan header VClaim. Mengembalikan isi response, null bila code bukan 200. */
    private JsonNode kirimBpjs(String url,String json) throws Exception{
        return kirimBpjs(HttpMethod.POST,url,json);
    }

    /** GET/POST ke BPJS dengan header VClaim. GET dikirim tanpa body. */
    private JsonNode kirimBpjs(HttpMethod metode,String url,String json) throws Exception{
        HttpHeaders headers=new HttpHeaders();
        // vclaim-sisrute-rest menolak form-urlencoded (415), wajib application/json
        headers.setContentType(MediaType.APPLICATION_JSON);
        String utc=String.valueOf(api.GetUTCdatetimeAsString());
        headers.add("X-Cons-ID",koneksiDB.CONSIDAPIBPJS());
        headers.add("X-Timestamp",utc);
        headers.add("X-Signature",api.getHmac(utc));
        headers.add("user_key",koneksiDB.USERKEYAPIBPJS());
        System.out.println("URL : "+link+url);
        System.out.println("JSON : "+json);
        // judul log: GetKriteriaRujukan/GetFaskesRujukan, referensi GET memakai path lengkap
        String judul=metode==HttpMethod.GET?url:url.substring(url.lastIndexOf("/")+1);
        String mentah;
        try {
            mentah=teks(api.getRest().exchange(link+url,metode,metode==HttpMethod.GET?new HttpEntity(headers):new HttpEntity(json,headers),byte[].class).getBody());
        } catch (Exception e) {
            catatError(judul,metode.name(),link+url,json,e);
            throw e;
        }
        catatBpjs(judul,metode.name(),link+url,json,mentah,utc);
        JsonNode root=mapper.readTree(mentah);
        JsonNode metadata=root.path("metaData");
        System.out.println("code : "+metadata.path("code").asText());
        System.out.println("message : "+metadata.path("message").asText());
        if(!metadata.path("code").asText().equals("200")){
            pesan=rapikanPesan(metadata.path("message").asText());
            return null;
        }
        return bacaResponse(root,utc);
    }

    /** Isi satuSehatRujukan yang sama untuk insert dan delete. Encounter tanpa awalan "Encounter/". */
    private ObjectNode nodeSatuSehat(String encounter,String pasien,String dokter,String faskestujuan,
                                     String instruksi,String keterangan){
        ObjectNode node=mapper.createObjectNode();
        node.put("kodeFaskesSatuSehat",kdfaskesasal);
        node.put("idPasienSatuSehat",nilai(pasien));
        node.put("kdppkSatuSehatTujuanRujukan",nilai(faskestujuan));
        node.put("kdDokterSatuSehat",nilai(dokter));
        node.putObject("encounter").put("reference",nilai(encounter));
        node.put("patientInstruction",nilai(instruksi));
        return node;
    }

    /** {"item":[{"linkId","text","answer":[{"valueBoolean"|"valueString"}]}]} */
    private ObjectNode nodeKriteria(){
        ObjectNode node=mapper.createObjectNode();
        ArrayNode item=node.putArray("item");
        for(Kriteria kriteria:daftarkriteria){
            ObjectNode isi=item.addObject();
            isi.put("linkId",kriteria.linkid);
            isi.put("text",kriteria.text);
            if(kriteria.isText()){
                isi.putArray("answer").addObject().put("valueString",kriteria.dipilih?kriteria.kodetindakan:"");
            }else{
                isi.putArray("answer").addObject().put("valueBoolean",kriteria.dipilih);
            }
        }
        return node;
    }

    private ObjectNode nodeWilayah(){
        ObjectNode node=mapper.createObjectNode();
        node.put("kodePropinsi",wilayah==null?"":wilayah.kodepropinsi);
        node.put("namaPropinsi",wilayah==null?"":wilayah.namapropinsi);
        node.put("kodeKabupaten",wilayah==null?"":wilayah.kodekabupaten);
        node.put("namaKabupaten",wilayah==null?"":wilayah.namakabupaten);
        return node;
    }

    /**
     * Isi response vclaim-sisrute-rest menjadi teks. Development mengirim "charset=utf-16" tanpa BOM
     * (little endian) sehingga tidak bisa dibaca langsung sebagai String oleh RestTemplate.
     */
    public static String teks(byte[] isi){
        if(isi==null||isi.length==0){
            return "";
        }
        try {
            if((isi.length>1)&&((isi[0]&0xff)==0xff)&&((isi[1]&0xff)==0xfe)){
                return new String(isi,2,isi.length-2,"UTF-16LE");
            }else if((isi.length>1)&&((isi[0]&0xff)==0xfe)&&((isi[1]&0xff)==0xff)){
                return new String(isi,2,isi.length-2,"UTF-16BE");
            }else if((isi.length>1)&&(isi[1]==0)){
                return new String(isi,"UTF-16LE");
            }else if((isi.length>1)&&(isi[0]==0)){
                return new String(isi,"UTF-16BE");
            }
            return new String(isi,"UTF-8");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            return "";
        }
    }

    private String nilai(String isi){
        return isi==null?"":isi;
    }

    private String pesanKoneksi(Exception e){
        System.out.println("Notifikasi Bridging : "+e);
        return pesanError(e);
    }

    /**
     * Pesan error untuk petugas. vclaim-sisrute-rest mengirim pesan di metaData.message walau HTTP 4xx/5xx,
     * contoh: "Response Satu Sehat: Data faskes dengan Org ID ... tidak ditemukan".
     */
    public static String pesanError(Exception e){
        if(e.toString().contains("UnknownHostException")){
            return "Koneksi ke server BPJS terputus...!";
        }
        if(e instanceof HttpStatusCodeException){
            try {
                String pesanbpjs=new ObjectMapper().readTree(teks(((HttpStatusCodeException)e).getResponseBodyAsByteArray()))
                        .path("metaData").path("message").asText();
                if(!pesanbpjs.equals("")){
                    return rapikanPesan(pesanbpjs);
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi : "+ex);
            }
        }
        return "Gagal menghubungi BPJS : "+e.getMessage();
    }

    /** Penanda pesan Satu Sehat yang hanya berisi resource Task tanpa alasan. */
    public static final String TANPA_KETERANGAN="Satu Sehat menolak permintaan tanpa keterangan";

    /**
     * Merapikan metaData.message BPJS agar mudah dibaca petugas. Pesan Satu Sehat berbentuk
     * "Response Satu Sehat: <alasan>; resource=Task/<id> sisrute_operation=<op> sisrute_status=<kode>"
     * dipisah menjadi alasan dan detail teknis untuk IT. Pesan panjang dipotong per baris.
     */
    public static String rapikanPesan(String pesan){
        if(pesan==null||pesan.trim().equals("")){
            return "";
        }
        String isi=pesan.trim();
        int awal=isi.indexOf("Response Satu Sehat:");
        if(awal<0){
            return "BPJS : "+potongBaris(isi);
        }
        isi=isi.substring(awal+"Response Satu Sehat:".length()).trim();
        String resource=ambilNilai(isi,"resource="),operasi=ambilNilai(isi,"sisrute_operation="),status=ambilNilai(isi,"sisrute_status=");
        String alasan=isi;
        if(alasan.contains("resource=")){
            alasan=alasan.substring(0,alasan.indexOf("resource="));
        }
        alasan=alasan.trim();
        while(alasan.endsWith(";")||alasan.endsWith(",")){
            alasan=alasan.substring(0,alasan.length()-1).trim();
        }
        if(alasan.matches("Task/[0-9a-fA-F-]+")){
            // hanya berisi resource tanpa alasan (contoh: tglRencanaKunjungan hari ini, jadwal faskes sudah lewat)
            if(resource.equals("")){
                resource=alasan;
            }
            alasan="";
        }
        StringBuilder hasil=new StringBuilder();
        if(alasan.equals("")){
            hasil.append(TANPA_KETERANGAN).append(".");
        }else{
            hasil.append("Satu Sehat menolak permintaan :\n").append(potongBaris(Character.toUpperCase(alasan.charAt(0))+alasan.substring(1)));
            if(!alasan.endsWith(".")){
                hasil.append(".");
            }
        }
        if(!(resource+operasi+status).equals("")){
            hasil.append("\n\nDetail untuk IT :");
            if(!resource.equals("")){
                hasil.append("\n- Resource : ").append(resource);
            }
            if(!operasi.equals("")){
                hasil.append("\n- Operasi  : ").append(operasi);
            }
            if(!status.equals("")){
                hasil.append("\n- Status   : ").append(status);
            }
        }
        return hasil.toString();
    }

    private static String ambilNilai(String teks,String kunci){
        int i=teks.indexOf(kunci);
        if(i<0){
            return "";
        }
        String nilai=teks.substring(i+kunci.length()).trim();
        int spasi=nilai.indexOf(' ');
        return (spasi<0?nilai:nilai.substring(0,spasi)).replace(";","").trim();
    }

    /** Memotong teks panjang per baris (maksimal 90 karakter) pada spasi agar jendela pesan tidak terlalu lebar. */
    private static String potongBaris(String teks){
        StringBuilder hasil=new StringBuilder();
        int panjang=0;
        for(String kata:teks.split(" ")){
            if((panjang>0)&&(panjang+kata.length()+1>90)){
                hasil.append("\n");
                panjang=0;
            }else if(panjang>0){
                hasil.append(" ");
                panjang++;
            }
            hasil.append(kata);
            panjang=panjang+kata.length();
        }
        return hasil.toString();
    }
}
