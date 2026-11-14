package modif;

import bridging.ApiBPJS;
import bridging.ApiSatuSehat;
import bridging.SatuSehatCekNIK;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.awt.Component;
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
    private static final String TABEL="bridging_rujukan_bpjs_satusehat";

    private final Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private final ApiBPJS api=new ApiBPJS();
    private final ObjectMapper mapper=new ObjectMapper();
    private String link="",pesan="";

    private String norawat="",nokartu="",idencounter="",idpasien="",iddokter="",kdfaskesasal="",
                   patientinstruction="",keteranganrujukan="",kddiagnosakriteria="",linkfhir="";
    private final List<Kriteria> daftarkriteria=new ArrayList<>();
    private final List<Wilayah> daftarwilayah=new ArrayList<>();
    private final List<Wilayah> daftarkabupaten=new ArrayList<>();
    private Wilayah wilayah=null;
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
        patientinstruction="";keteranganrujukan="";
        daftarkriteria.clear();
        daftarwilayah.clear();
        daftarkabupaten.clear();
        wilayah=null;
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

            ps=koneksi.prepareStatement("select no_rawat,no_kartu from bridging_sep where no_sep=?");
            try {
                ps.setString(1,nosep);
                rs=ps.executeQuery();
                if(rs.next()){
                    adasep=true;
                    norawatsep=rs.getString(1).trim();
                    nokartu=rs.getString(2).trim();
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
        wilayah=null;
        faskestujuan=null;
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
        } catch (Exception e) {
            pesan=pesanKoneksi(e);
        }
        return pesan.equals("")&&!daftarkriteria.isEmpty();
    }

    /**
     * Menampilkan jendela kemampuan layanan / kriteria rujukan dan pilihan wilayah jejaring.
     * Hanya satu kriteria yang boleh dipilih (aturan Satu Sehat: salah satu dari Terapi, Tindakan Medis,
     * atau Upaya Diagnosis), kolom Pilih bekerja seperti radio. Bila Tindakan Medis dipilih, kode ICD-9 wajib diisi,
     * bisa dicari lewat tombol "Cari ICD-9" (referensi prosedur VClaim).
     * @return true bila petugas menekan OK dan isian lengkap
     */
    public boolean pilihKriteria(Component parent){
        if(daftarkriteria.isEmpty()){
            pesan="Ambil kriteria rujukan dulu";
            JOptionPane.showMessageDialog(parent,pesan);
            return false;
        }
        DlgKriteriaRujukanSatuSehat kriteria=new DlgKriteriaRujukanSatuSehat(daftarkriteria,daftarwilayah,daftarkabupaten,wilayah,
                koneksiDB.PROPINSISATUSEHAT(),nokartu,idpasien,idencounter,kddiagnosakriteria);
        kriteria.setVisible(true);
        if(kriteria.isDipilih()){
            wilayah=kriteria.getWilayah();
            faskestujuan=null;
            return true;
        }
        return false;
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
        if(!kriteriaLengkap()){
            pesan="Kriteria rujukan dan wilayah jejaring belum diisi";
            return hasil;
        }
        try {
            ObjectNode body=mapper.createObjectNode();
            body.put("kodeFaskesSatuSehat",kdfaskesasal);
            body.put("kodeDiagnosa",kddiagnosa);
            body.put("kodeSpesialis",kdspesialis);
            body.put("tglRencanaKunjungan",tglrencana);
            body.set("kriteriaRujukan",nodeKriteria());
            body.set("codeJejaringWilayah",nodeWilayah());
            body.putObject("encounter").put("reference","Encounter/"+idencounter);
            JsonNode response=kirimBpjs(URL_FASKES,mapper.writeValueAsString(body));
            if(response!=null){
                for(JsonNode list:response.path("list")){
                    Faskes faskes=new Faskes();
                    // development mengirim "Organization/100026146", insert memakai angkanya saja
                    faskes.kodesatusehat=list.path("kodeFaskesSatuSehat").asText().replace("Organization/","");
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
            if(pesan.matches("(?s).*Response Satu Sehat: Task/[0-9a-fA-F-]+\\s*")){
                pesan=pesan+"\n\nSatu Sehat tidak memberi keterangan. Kemungkinan tidak ada jadwal layanan faskes pada "+
                      "tanggal rencana kunjungan "+tglrencana+", coba tanggal rencana kunjungan berikutnya.";
            }
        }
        return hasil;
    }

    /**
     * Mencari faskes tujuan lalu menampilkan daftarnya untuk dipilih (dobel klik atau tombol Pilih).
     * @return faskes yang dipilih, null bila batal atau gagal (pesan sudah ditampilkan)
     */
    public Faskes pilihFaskes(Component parent,String kddiagnosa,String kdspesialis,String tglrencana){
        List<Faskes> daftarfaskes=cariFaskes(kddiagnosa,kdspesialis,tglrencana);
        if(daftarfaskes.isEmpty()){
            JOptionPane.showMessageDialog(parent,pesan);
            return null;
        }
        DlgFaskesRujukanSatuSehat faskes=new DlgFaskesRujukanSatuSehat(daftarfaskes,
                "Diagnosa "+kddiagnosa+", Spesialis "+kdspesialis+", Rencana "+tglrencana+", Wilayah "+
                wilayah.namapropinsi+(wilayah.namakabupaten.equals("")?"":" / "+wilayah.namakabupaten));
        faskes.setVisible(true);
        if(faskes.getTerpilih()!=null){
            faskestujuan=faskes.getTerpilih();
        }
        return faskes.getTerpilih();
    }

    /** Faskes tujuan yang terakhir dipilih, null bila belum ada. */
    public Faskes getFaskesTujuan(){
        return faskestujuan;
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
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            return "{}";
        }
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
        return !daftarkriteria.isEmpty()&&(wilayah!=null);
    }

    /** POST ke BPJS dengan header VClaim. Mengembalikan isi response, null bila code bukan 200. */
    private JsonNode kirimBpjs(String url,String json) throws Exception{
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
        String mentah;
        try {
            mentah=teks(api.getRest().exchange(link+url,HttpMethod.POST,new HttpEntity(json,headers),byte[].class).getBody());
        } catch (Exception e) {
            catatError(url.substring(url.lastIndexOf("/")+1),"POST",link+url,json,e);
            throw e;
        }
        catatBpjs(url.substring(url.lastIndexOf("/")+1),"POST",link+url,json,mentah,utc);
        JsonNode root=mapper.readTree(mentah);
        JsonNode metadata=root.path("metaData");
        System.out.println("code : "+metadata.path("code").asText());
        System.out.println("message : "+metadata.path("message").asText());
        if(!metadata.path("code").asText().equals("200")){
            pesan=metadata.path("message").asText();
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
                    return "BPJS : "+pesanbpjs;
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi : "+ex);
            }
        }
        return "Gagal menghubungi BPJS : "+e.getMessage();
    }
}
