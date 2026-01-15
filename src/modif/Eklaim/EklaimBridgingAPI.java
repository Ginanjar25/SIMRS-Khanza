/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modif.Eklaim;

import AESsecurity.EnkripsiAES;
import bridging.ApiEklaimConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author IT
 */
public class EklaimBridgingAPI {
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private String json = "", reqjson = "", request = "", link = "", nol_jam = "", nol_menit = "", nol_detik = "", jam = "", menit = "", detik = "", iddokter = "", idpasien = "", signa1 = "1", signa2 = "1", evaluasi = "", key = "", kelasRS = "", coder_nik = "";
    private ApiEklaimConfig api = new ApiEklaimConfig();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
//    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response, nameNode;
    private PreparedStatement ps;
    private ResultSet rs;
    private String[] arrSplit;
    private SimpleDateFormat tanggalFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date date = new Date();
    private static final Properties prop = new Properties();
    private static String var = "";
    private String biayaReg = "", prosedur = "", diagnosa = "", discharge_status = "5", dokter = "", naikKelas = "", sistole = "", diastole = "", gender = "", upgrade_class_ind = "", birth_weight = "", kls_rawat = "", cara_masuk = "";
    private Boolean result = false, initklaim=false;
    public static final ObjectMapper mapper = new ObjectMapper();
    private String bridging_eklaim = "";
    
    
    public static class TarifRs {
        public int prosedurNonBedah;
        public int prosedurBedah;
        public int konsultasi;
        public int keperawatan;
        public int radiologi;
        public int laboratorium;
        public int kamar;
        public int obat;
        public int obatKronis;
        public int obatKemoterapi;
        public int bmhp;
        public int sewaAlat;
        public int rehabilitasi;
        public int tenagaAhli;
    }

    public EklaimBridgingAPI() {
        try {
            key = "8e96e280a56691be888656b7e945023c2f50ec5a09b38b0b96e7acf22d2f5582";
            link = "http://192.168.106.100/E-Klaim/ws.php";
            kelasRS = "CS";
            coder_nik = CODERNIK();
            bridging_eklaim = BRIDGINGEKLAIM();
        } catch (Exception e) {
            System.out.println("Notif init Eklaim : " + e);
        }
    }
    
    public void bridgingInit(String no_sep) {
        if (bridging_eklaim.equals("yes")) {
            try {
                ps = koneksi.prepareStatement(
                        "select bridging_sep.no_sep, bridging_sep.no_rawat,bridging_sep.nomr,bridging_sep.nama_pasien, "
                        + "bridging_sep.tglsep,bridging_sep.tglrujukan,bridging_sep.no_rujukan,bridging_sep.kdppkrujukan, "
                        + "bridging_sep.nmppkrujukan,bridging_sep.kdppkpelayanan,bridging_sep.nmppkpelayanan,bridging_sep.jnspelayanan, "
                        + "if(bridging_sep.jnspelayanan='1','1. Rawat Inap','2. Rawat Jalan') as jenispelayanan,bridging_sep.catatan,bridging_sep.diagawal, "
                        + "bridging_sep.nmdiagnosaawal,bridging_sep.kdpolitujuan,bridging_sep.nmpolitujuan,bridging_sep.klsrawat, "
                        + "if(bridging_sep.klsrawat='1','1. Kelas 1',if(bridging_sep.klsrawat='2','2. Kelas 2','3. Kelas 3')) as kelas, "
                        + "if(bridging_sep.lakalantas='1','1. Kasus Kecelakaan','2. Bukan Kasus Kecelakaan') as lakalantas,bridging_sep.user, "
                        + "bridging_sep.tanggal_lahir,bridging_sep.peserta,bridging_sep.jkel,bridging_sep.no_kartu, "
                        + "if(bridging_sep.tglpulang='00-00-0000 00:00:00',now(),bridging_sep.tglpulang) as tglpulang, "
                        + "inacbg_data_terkirim.tgl_terkirim, inacbg_data_terkirim.code, inacbg_data_terkirim.message, "
                        + "inacbg_klaim_baru.patient_id, inacbg_klaim_baru.admission_id, inacbg_klaim_baru.hospital_admission_id, reg_periksa.stts, bridging_sep.catatan "
                        + "from bridging_sep "
                        + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = bridging_sep.no_rawat "
                        + "LEFT JOIN inacbg_data_terkirim ON inacbg_data_terkirim.no_sep = bridging_sep.no_sep "
                        + "LEFT JOIN inacbg_klaim_baru ON inacbg_klaim_baru.no_sep = bridging_sep.no_sep "
                        + "where bridging_sep.no_sep = ? "
                );
                try {
                    ps.setString(1, no_sep);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        initklaim = klaimBaru(rs.getString("no_kartu"), rs.getString("no_sep"), rs.getString("nomr"), rs.getString("nama_pasien"), rs.getString("tanggal_lahir"), rs.getString("jkel").equals("L") ? "1" : "2");

                        if (initklaim) {
                            updateDataKlaim(rs.getString("no_sep"), rs.getString("no_kartu"), rs.getString("tglsep"), rs.getString("tglpulang"), rs.getString("jnspelayanan"), rs.getString("klsrawat"), rs.getString("no_rawat"));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Gagal update klaim SEP " + no_sep);
                    e.printStackTrace();
                }

            } catch (Exception e) {
                System.err.println("Gagal update klaim SEP " + no_sep);
                e.printStackTrace();
            }
        }
    }
    
    public Boolean klaimBaru(String nomor_kartu, String nomor_sep, String nomor_rm, String nama_pasien, String tgl_lahir, String gender) {

        try {
            int exist = Sequel.cariInteger("SELECT patient_id, admission_id, hospital_admission_id FROM inacbg_klaim_baru WHERE no_sep = ?", nomor_sep);

            if (exist != 0) {
                return true;
            }

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            json = "{"
                    + "\"metadata\":{"
                    + "\"method\":\"new_claim\""
                    + "},"
                    + "\"data\":{"
                    + "\"nomor_kartu\":\"" + nomor_kartu + "\","
                    + "\"nomor_sep\":\"" + nomor_sep + "\","
                    + "\"nomor_rm\":\"" + nomor_rm + "\","
                    + "\"nama_pasien\":\"" + nama_pasien + "\","
                    + "\"tgl_lahir\":\"" + tgl_lahir + "\","
                    + "\"gender\":\"" + gender + "\""
                    + "}"
                    + "}";

            String encryptedJson = api.Encrypt(json, api.getKey());
            requestEntity = new HttpEntity<>(encryptedJson, headers);
            String responseJson = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
            String decryptedJson = api.Decrypt(responseJson, api.getKey());
            System.out.println(decryptedJson);

            root = mapper.readTree(decryptedJson);
            JsonNode metadata = root.path("metadata");
            String code = metadata.path("code").asText();

            if (code.equals("200")) {
                JsonNode responseNode = root.path("response");
                String patientId = responseNode.path("patient_id").asText();
                String admissionId = responseNode.path("admission_id").asText();
                String hospitalAdmissionId = responseNode.path("hospital_admission_id").asText();

                Sequel.menyimpantf("inacbg_klaim_baru", "?,?,?,?", "INACBG KLAIM BARU", 4, new String[]{
                    nomor_sep, patientId, admissionId, hospitalAdmissionId
                });

                return true;
            } else {
                System.out.println("GAGAL BUAT KLAIM BARU NO SEP : " + nomor_sep);
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }
    
    public void updateDataKlaim(String no_sep, String no_kartu, String tgl_masuk, String tgl_pulang, String jenis_rawat, String kelas_rawat, String no_rawat) {
        try {
            
            if(Sequel.cariIsi("select status from bridging_eklaim where no_sep = ?", no_sep).equals("0")){
                EditUlangKlaim(no_sep);
            }
            
            discharge_status = executeQuery(no_rawat, "discharge_status");
            dokter = executeQuery(no_rawat, "dokter");
            cara_masuk = executeQuery(no_sep, "cara_masuk");
            naikKelas = executeQuery(no_sep, "naik_kelas");
            biayaReg = executeQuery(no_rawat, "biaya_reg");

            upgrade_class_ind = naikKelas.isEmpty() ? "0" : "1";
            String upgrade_class_class = naikKelas;
            String upgrade_class_los = "";
            String add_payment_pct = "";

            String adl_sub_acute = "";
            String adl_chronic = "";
            String icu_indikator = "";
            String icu_los = "";
            String ventilator_hour = "";

            if (jenis_rawat.equals("1")) {
                kls_rawat = rs.getString("klsrawat");
                String tensiResult = Sequel.cariIsi("SELECT pemeriksaan_ranap.tensi FROM pemeriksaan_ranap WHERE pemeriksaan_ranap.no_rawat=? ORDER BY pemeriksaan_ranap.tgl_perawatan DESC, pemeriksaan_ranap.jam_rawat DESC limit 1", no_rawat);
                if (tensiResult != null && !tensiResult.isEmpty()) {
                    String[] tensi = tensiResult.split("/");
                    if (tensi.length > 0 && !tensi[0].equals("-")) {
                        sistole = tensi[0];
                    }
                    if (tensi.length > 1) {
                        diastole = tensi[1];
                    }
                } else {
                    sistole = "120";
                    diastole = "90";
                }
            } else {
                kls_rawat = "3";
                String tensiResult = Sequel.cariIsi("select pemeriksaan_ralan.tensi from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat=? ORDER BY pemeriksaan_ralan.tgl_perawatan DESC, pemeriksaan_ralan.jam_rawat DESC limit 1", no_rawat);
                if (tensiResult != null && !tensiResult.isEmpty()) {
                    String[] tensi = tensiResult.split("/");
                    if (tensi.length > 0 && !tensi[0].equals("-")) {
                        sistole = tensi[0];
                    }
                    if (tensi.length > 1) {
                        diastole = tensi[1];
                    }
                } else {
                    sistole = "120";
                    diastole = "90";
                }
            }

            birth_weight = Sequel.cariIsi(
                    "select berat_badan from pasien_bayi where no_rawat=?",
                    no_rawat
            );

            TarifRs tarif = hitungTarifRs(no_rawat);

            String tgl_kirim = tgl_pulang;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"));

            // Mendapatkan tanggal dan waktu saat ini
            Date now = new Date();

            // Format tanggal dan waktu ke dalam string
            String tgl_realtime = dateFormat.format(now);

            try {
                request = "{"
                        + "\"metadata\":{"
                        + "\"method\":\"set_claim_data\","
                        + "\"nomor_sep\":\"" + no_sep + "\""
                        + "},"
                        + "\"data\":{"
                        + "\"nomor_sep\":\"" + no_sep + "\","
                        + "\"nomor_kartu\":\"" + no_kartu + "\","
                        + "\"tgl_masuk\":\"" + tgl_masuk + "\","
                        + "\"tgl_pulang\":\"" + tgl_kirim + "\","
                        + "\"cara_masuk\":\"" + cara_masuk + "\","
                        + "\"jenis_rawat\":\"" + jenis_rawat + "\","
                        + "\"kelas_rawat\":\"" + kelas_rawat + "\","
                        + "\"adl_sub_acute\":\"" + adl_sub_acute + "\","
                        + "\"adl_chronic\":\"" + adl_chronic + "\","
                        + "\"icu_indikator\":\"" + icu_indikator + "\","
                        + "\"icu_los\":\"" + icu_los + "\","
                        + "\"ventilator_hour\":\"" + ventilator_hour + "\","
                        + "\"upgrade_class_ind\":\"" + upgrade_class_ind + "\","
                        + "\"upgrade_class_class\":\"" + upgrade_class_class + "\","
                        + "\"upgrade_class_los\":\"" + upgrade_class_los + "\","
                        + "\"add_payment_pct\":\"" + add_payment_pct + "\","
                        + "\"birth_weight\":\"" + birth_weight + "\","
                        + "\"sistole\":\"" + sistole + "\","
                        + "\"diastole\":\"" + diastole + "\","
                        + "\"discharge_status\":\"" + discharge_status + "\","
                        + "\"tarif_rs\": {"
                        + "\"prosedur_non_bedah\":\"" + tarif.prosedurNonBedah + "\","
                        + "\"prosedur_bedah\":\"" + tarif.prosedurBedah + "\","
                        + "\"konsultasi\":\"" + tarif.konsultasi + "\","
                        + "\"tenaga_ahli\":\"" + tarif.tenagaAhli + "\","
                        + "\"keperawatan\":\"" + tarif.keperawatan + "\","
                        + "\"penunjang\":\"0\","
                        + "\"radiologi\":\"" + tarif.radiologi + "\","
                        + "\"laboratorium\":\"" + tarif.laboratorium + "\","
                        + "\"pelayanan_darah\":\"0\","
                        + "\"rehabilitasi\":\"" + tarif.rehabilitasi + "\","
                        + "\"kamar\":\"" + tarif.kamar + "\","
                        + "\"rawat_intensif\":\"0\","
                        + "\"obat\":\"" + tarif.obat + "\","
                        + "\"obat_kronis\":\"" + tarif.obatKronis + "\","
                        + "\"obat_kemoterapi\":\"" + tarif.obatKemoterapi + "\","
                        + "\"alkes\":\"0\","
                        + "\"bmhp\":\"" + tarif.bmhp + "\","
                        + "\"sewa_alat\":\"" + tarif.sewaAlat + "\""
                        + "},"
                        + "\"payor_id\":\"0\","
                        + "\"nama_dokter\":\"" + dokter + "\","
                        + "\"kode_tarif\":\"" + kelasRS + "\","
                        + "\"payor_id\":\"3\","
                        + "\"payor_cd\":\"JKN\","
                        + "\"cob_cd\":\"#\","
                        + "\"coder_nik\":\"" + coder_nik + "\""
                        + "}"
                        + "}";

                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                // Enkripsi JSON sebelum mengirimkan
                String encryptedJson = api.Encrypt(request, api.getKey());
                requestEntity = new HttpEntity<>(encryptedJson, headers);
                String responseJson = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
                String decryptedJson = api.Decrypt(responseJson, api.getKey());
//                System.out.println(decryptedJson);
                root = mapper.readTree(decryptedJson);
                JsonNode metadata = root.path("metadata");
                String code = metadata.path("code").asText();
                String message = metadata.path("message").asText();
                int total_tarif = Sequel.cariInteger("select SUM(billing.totalbiaya) AS total_biaya from billing where billing.no_rawat=?", no_rawat);
                if (code.equals("200") && total_tarif > 25000) {
                    Sequel.menyimpantf("inacbg_data_terkirim", "?,?,?,?,?,?", "INACBG KLAIM TERKIRIM", 6, new String[]{
                        no_sep, coder_nik, tgl_realtime, code, message, request
                    });
                    setIDRG(no_sep, no_rawat, cara_masuk);
                } else if (code.equals("400") && total_tarif > 25000) {
                    Sequel.menyimpantf("inacbg_data_terkirim", "?,?,?,?,?,?", "INACBG KLAIM TERKIRIM", 6, new String[]{
                        no_sep, coder_nik, tgl_realtime, code, message, request
                    });
                }
                saveDataKlaim(no_rawat,no_sep, no_kartu, tgl_masuk, tgl_kirim, cara_masuk, jenis_rawat, kelas_rawat, adl_sub_acute, adl_chronic, icu_indikator, icu_los, ventilator_hour,
                        upgrade_class_ind, upgrade_class_class, upgrade_class_los, "", add_payment_pct, birth_weight, sistole, diastole, discharge_status, tarif, "0", dokter,
                        kelasRS, "3", "JKN", "#", coder_nik);


            } catch (Exception e) {
                System.err.println("Gagal update klaim SEP " + no_sep);
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Gagal update klaim SEP " + no_sep);
            e.printStackTrace();
        }
    }
    
    private boolean setIDRG(String nomor_sep, String no_rawat, String jp) {

        if (nomor_sep == null || nomor_sep.trim().isEmpty()) {
            return false;
        }

        // ===== AMBIL DATA =====
        diagnosa = jp.equals("1")
                ? executeQuery(no_rawat, "penyakit")
                : executeQuery(no_rawat, "icd-9");

        prosedur = jp.equals("1")
                ? executeQuery(no_rawat, "prosedur")
                : executeQuery(no_rawat, "icd-10");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper = new ObjectMapper();

            // ================= SET DIAGNOSA =================
            if (diagnosa != null && !diagnosa.trim().isEmpty()) {

                String jsonDiagnosa
                        = "{"
                        + "\"metadata\":{"
                        + "\"method\":\"idrg_diagnosa_set\","
                        + "\"nomor_sep\":\"" + nomor_sep + "\""
                        + "},"
                        + "\"data\":{"
                        + "\"diagnosa\":\"" + diagnosa + "\""
                        + "}"
                        + "}";

                String encrypted = api.Encrypt(jsonDiagnosa, api.getKey());
                HttpEntity<String> request = new HttpEntity<>(encrypted, headers);

                String responseJson = api.getRest()
                        .exchange(link, HttpMethod.POST, request, String.class)
                        .getBody();

                if (responseJson == null || responseJson.trim().isEmpty()) {
                    return false;
                }

                String decrypted = api.Decrypt(responseJson, api.getKey());
                JsonNode root = mapper.readTree(decrypted);
                JsonNode metadata = root.path("metadata");

                if (!metadata.path("code").asText().equals("200")) {
                    return false;
                }
            } else {
                System.out.println("setIDRG diagnosa skip (kosong)");
            }

            // ================= SET PROSEDUR =================
            if (prosedur != null && !prosedur.trim().isEmpty()) {

                String jsonProsedur
                        = "{"
                        + "\"metadata\":{"
                        + "\"method\":\"idrg_procedure_set\","
                        + "\"nomor_sep\":\"" + nomor_sep + "\""
                        + "},"
                        + "\"data\":{"
                        + "\"procedure\":\"" + prosedur + "\""
                        + "}"
                        + "}";

                String encrypted = api.Encrypt(jsonProsedur, api.getKey());
                HttpEntity<String> request = new HttpEntity<>(encrypted, headers);

                String responseJson = api.getRest()
                        .exchange(link, HttpMethod.POST, request, String.class)
                        .getBody();

                if (responseJson == null || responseJson.trim().isEmpty()) {
                    return false;
                }

                String decrypted = api.Decrypt(responseJson, api.getKey());
                JsonNode root = mapper.readTree(decrypted);
                JsonNode metadata = root.path("metadata");

                if (!metadata.path("code").asText().equals("200")) {
                    return false;
                }
                
                saveIDRGToDB(nomor_sep, diagnosa, prosedur);
            } else {
                System.out.println("setIDRG prosedur skip (kosong)");
            }

            // ===== JIKA SAMPAI SINI, SEMUA AMAN =====
            return true;

        } catch (Exception e) {
            System.out.println("Error setIDRG: " + e.getMessage());
            return false;
        }
    }
    
    private void EditUlangKlaim(String nomor_sep) {

        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Buat JSON request
            json = "{"
                    + "\"metadata\":{"
                    + "\"method\":\"reedit_claim\""
                    + "},"
                    + "\"data\":{"
                    + "\"nomor_sep\":\"" + nomor_sep + "\""
                    + "}"
                    + "}";

            // Enkripsi JSON sebelum mengirimkan
            String encryptedJson = api.Encrypt(json, api.getKey());
            requestEntity = new HttpEntity<>(encryptedJson, headers);
            String responseJson = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
            // Dekripsi JSON setelah menerima respon
            String decryptedJson = api.Decrypt(responseJson, api.getKey());
        } catch (Exception ea) {
            System.out.println("Notifikasi Bridging : " + ea);
        }
    }
    
    public TarifRs hitungTarifRs(String no_rawat) throws Exception {

        TarifRs t = new TarifRs();

        String sql
                = "SELECT "
                + "SUM(CASE WHEN status IN ('Ralan Dokter Paramedis','Ranap Dokter Paramedis') "
                + "AND nm_perawatan NOT LIKE '%terapi%' THEN totalbiaya ELSE 0 END) AS prosedur_non_bedah, "
                + "SUM(CASE WHEN status='Operasi' THEN totalbiaya ELSE 0 END) AS prosedur_bedah, "
                + "SUM(CASE WHEN status IN ('Ranap Dokter','Ralan Dokter') THEN totalbiaya ELSE 0 END) AS konsultasi, "
                + "SUM(CASE WHEN status IN ('Ranap Paramedis','Ralan Paramedis') THEN totalbiaya ELSE 0 END) AS keperawatan, "
                + "SUM(CASE WHEN status='Radiologi' THEN totalbiaya ELSE 0 END) AS radiologi, "
                + "SUM(CASE WHEN status='Laborat' THEN totalbiaya ELSE 0 END) AS laboratorium, "
                + "SUM(CASE WHEN status='Kamar' THEN totalbiaya ELSE 0 END) AS kamar, "
                + "SUM(CASE WHEN status IN ('Obat','Retur Obat','Resep Pulang') THEN totalbiaya ELSE 0 END) AS obat_total, "
                + "SUM(CASE WHEN status='Obat' AND nm_perawatan LIKE '%kronis%' THEN totalbiaya ELSE 0 END) AS obat_kronis, "
                + "SUM(CASE WHEN status='Obat' AND nm_perawatan LIKE '%kemo%' THEN totalbiaya ELSE 0 END) AS obat_kemo, "
                + "SUM(CASE WHEN status='Tambahan' THEN totalbiaya ELSE 0 END) AS bmhp, "
                + "SUM(CASE WHEN status IN ('Harian','Service') THEN totalbiaya ELSE 0 END) AS sewa_alat, "
                + "SUM(CASE WHEN status IN ('Ralan Dokter Paramedis','Ranap Dokter Paramedis') "
                + "AND nm_perawatan LIKE '%terapi%' THEN totalbiaya ELSE 0 END) AS rehabilitasi "
                + "FROM billing WHERE no_rawat=?";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, no_rawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t.prosedurNonBedah = rs.getInt("prosedur_non_bedah");
                    t.prosedurBedah = rs.getInt("prosedur_bedah");
                    t.konsultasi = rs.getInt("konsultasi");
                    t.keperawatan = rs.getInt("keperawatan");
                    t.radiologi = rs.getInt("radiologi");
                    t.laboratorium = rs.getInt("laboratorium");
                    t.kamar = rs.getInt("kamar") + Sequel.cariInteger("SELECT biaya_reg FROM reg_periksa WHERE no_rawat = ?", no_rawat);

                    int obatTotal = rs.getInt("obat_total");
                    t.obatKronis = rs.getInt("obat_kronis");
                    t.obatKemoterapi = rs.getInt("obat_kemo");
                    t.obat = obatTotal - t.obatKronis - t.obatKemoterapi;

                    t.bmhp = rs.getInt("bmhp");
                    t.sewaAlat = rs.getInt("sewa_alat");
                    t.rehabilitasi = rs.getInt("rehabilitasi");
                }
            }
        }
        return t;
    }
     
    
      private String executeQuery(String param, String data) {
        String hasil = "";

        try {
            switch (data) {
                case "biaya_reg":
                    hasil = Sequel.cariIsi("SELECT biaya_reg FROM reg_periksa WHERE no_rawat = ?", param);
                    break;

                case "prosedur":
                    hasil = executeQueryWithPrioritas(param, "prosedur_pasien");
                    break;
                case "penyakit":
                    hasil = executeQueryWithPrioritas(param, "diagnosa_pasien");
                    break;

                case "icd-9":
                    hasil = getICDCode(param, "U0016", "U0014", "U0006", "Z50.9", "Z09.8");
                    break;

                case "icd-10":
                    hasil = getICDCode(param, "U0016", null, null, "93.39", "");
                    break;

                case "discharge_status":
                    hasil = getDischargeStatus(param);
                    break;

                case "dokter":
                    hasil = getDokterName(param);
                    break;

                case "naik_kelas":
                    hasil = getNaikKelas(param);
                    break;

                case "cara_masuk":
                    hasil = getCaraMasuk(param);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        return hasil;
    }

    private String executeQueryWithPrioritas(String param, String tableName) throws SQLException {
        String query = "";
        if (tableName.equals("prosedur_pasien")) {
            query = String.format("SELECT kode FROM %s WHERE no_rawat = ? ORDER BY prioritas ASC", tableName);
        } else {
            query = String.format("SELECT kd_penyakit as kode FROM %s WHERE no_rawat = ? ORDER BY prioritas ASC", tableName);
        }
        PreparedStatement preparedStatement = koneksi.prepareStatement(query);
        preparedStatement.setString(1, param);

        ResultSet resultSet = preparedStatement.executeQuery();
        StringBuilder hasil = new StringBuilder();
        int counter = 0;

        while (resultSet.next()) {
            if (counter > 0) {
                hasil.append("#");
            }
            hasil.append(resultSet.getString("kode"));
            counter++;
        }

        return hasil.toString();
    }

    private String getICDCode(String param, String poliCheck, String poliIgnore1, String poliIgnore2, String code1, String code2) throws SQLException {
        String poli = Sequel.cariIsi("SELECT kd_poli FROM reg_periksa WHERE no_rawat = ?", param);
        if (poli.equals(poliCheck)) {
            return code1;
        } else if (poli.equals("IGDK")) {
            return "";
        } else if (poliIgnore2 != null && poli.equals(poliIgnore2)) {
            return "";
        } else if (poliIgnore1 != null && poli.equals(poliIgnore1)) {
            String operasi = Sequel.cariIsi("SELECT b.nm_perawatan FROM billing b WHERE b.no_rawat =? AND b.`status` = 'Operasi' AND b.biaya <> 0", param);
            if (operasi.isBlank() || operasi.isEmpty()) {
                return code2;
            } else {
                return "";
            }
        } else {
            return code2;
        }
    }

    private String getDischargeStatus(String param) throws SQLException {
        if (Sequel.cariInteger("SELECT COUNT(no_rawat) FROM kamar_inap WHERE stts_pulang IN ('Sembuh', 'Sehat', 'Atas Persetujuan Dokter') AND no_rawat = ?", param) > 0) {
            return "1";
        } else if (Sequel.cariInteger("SELECT COUNT(no_rawat) FROM kamar_inap WHERE stts_pulang = 'Rujuk' AND no_rawat = ?", param) > 0) {
            return "2";
        } else if (Sequel.cariInteger("SELECT COUNT(no_rawat) FROM kamar_inap WHERE stts_pulang IN ('APS', 'Pulang Paksa', 'Atas Permintaan Sendiri') AND no_rawat = ?", param) > 0) {
            return "3";
        } else if (Sequel.cariInteger("SELECT COUNT(no_rawat) FROM kamar_inap WHERE stts_pulang IN ('Meninggal', '+') AND no_rawat = ?", param) > 0) {
            return "4";
        } else if (Sequel.cariInteger("SELECT COUNT(no_rawat) FROM kamar_inap WHERE stts_pulang = 'Lain-lain' AND no_rawat = ?", param) > 0) {
            return "5";
        } else {
            return "1";
        }
    }

    private String getDokterName(String param) throws SQLException {
        StringBuilder hasil = new StringBuilder();
        String query = "SELECT dokter.nm_dokter FROM dpjp_ranap INNER JOIN dokter ON dpjp_ranap.kd_dokter = dokter.kd_dokter WHERE dpjp_ranap.no_rawat = ?";
        PreparedStatement preparedStatement = koneksi.prepareStatement(query);
        preparedStatement.setString(1, param);

        ResultSet resultSet = preparedStatement.executeQuery();
        int counter = 0;
        while (resultSet.next()) {
            if (counter > 0) {
                hasil.append("#");
            }
            hasil.append(resultSet.getString("nm_dokter"));
            counter++;
        }

        if (hasil.length() == 0) {
            hasil.append(Sequel.cariIsi("SELECT dokter.nm_dokter FROM reg_periksa INNER JOIN dokter ON reg_periksa.kd_dokter = dokter.kd_dokter WHERE reg_periksa.no_rawat = ?", param));
        }

        return hasil.toString();
    }

    private String getCaraMasuk(String param) throws SQLException {
        String cara_masuk = Sequel.cariIsi("SELECT catatan FROM bridging_sep WHERE no_sep = ?", param).toUpperCase().replaceAll("[^A-Z]", "");
        if (cara_masuk != null && !cara_masuk.isEmpty()) {
            switch (cara_masuk) {
                case "FKTP":
                    return "gp";
                case "SKDP":
                    return "outp";
                case "INTERNAL":
                    return "mp";
                case "POSTRI":
                    return "inp";
                case "FKTRL":
                    return "hosp-trans";
                case "IGD":
                    return "emd";
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    private String getNaikKelas(String param) throws SQLException {
        String klsnaik = Sequel.cariIsi("SELECT klsnaik FROM bridging_sep WHERE no_sep = ?", param);
        if (klsnaik != null && !klsnaik.isEmpty()) {
            switch (klsnaik) {
                case "1":
                    return "vvip";
                case "2":
                    return "vip";
                case "3":
                    return "kelas_1";
                case "4":
                    return "kelas_2";
                case "8":
                    return "vip";
                default:
                    return "";
            }
        } else {
            return "";
        }
    }
    
    private void saveDataKlaim(
            String no_rawat,
            String nomor_sep,
            String nomor_kartu,
            String tgl_masuk,
            String tgl_pulang,
            String cara_masuk,
            String jenis_rawat,
            String kelas_rawat,
            String adl_sub_acute,
            String adl_chronic,
            String icu_indikator,
            String icu_los,
            String ventilator_hour,
            String upgrade_class_ind,
            String upgrade_class_class,
            String upgrade_class_los,
            String upgrade_class_payor,
            String add_payment_pct,
            String birth_weight,
            String sistole,
            String diastole,
            String discharge_status,
            TarifRs tarif,
            String tarif_poli_eks,
            String nama_dokter,
            String kode_tarif,
            String payor_id,
            String payor_cd,
            String cob_cd,
            String coder_nik
    ) {
        try {
            String now = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", new java.util.Locale("id", "ID")
            ).format(new java.util.Date());

            boolean exists = Sequel.cariInteger(
                    "select count(*) from bridging_eklaim where no_sep=?",
                    nomor_sep
            ) > 0;

            boolean closed_billing = Sequel.cariInteger("select SUM(billing.totalbiaya) AS total_biaya from billing where billing.no_rawat=?", no_rawat) > 25000;

            if (!exists) {
                // ================= INSERT =================
                Sequel.menyimpantf(
                        "bridging_eklaim",
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                        "INSERT BRIDGING EKLAIM",
                        49,
                        new String[]{
                            nomor_sep, nomor_kartu, tgl_masuk, tgl_pulang,
                            cara_masuk, jenis_rawat, kelas_rawat,
                            adl_sub_acute, adl_chronic, icu_indikator, icu_los, ventilator_hour,
                            upgrade_class_ind, upgrade_class_class, upgrade_class_los, upgrade_class_payor,
                            add_payment_pct, birth_weight,
                            sistole, diastole, discharge_status,
                            // tarif
                            String.valueOf(tarif.prosedurNonBedah),
                            String.valueOf(tarif.prosedurBedah),
                            String.valueOf(tarif.konsultasi),
                            String.valueOf(tarif.tenagaAhli),
                            String.valueOf(tarif.keperawatan),
                            "0",
                            String.valueOf(tarif.radiologi),
                            String.valueOf(tarif.laboratorium),
                            "0",
                            String.valueOf(tarif.rehabilitasi),
                            String.valueOf(tarif.kamar),
                            "0",
                            String.valueOf(tarif.obat),
                            String.valueOf(tarif.obatKronis),
                            String.valueOf(tarif.obatKemoterapi),
                            "0",
                            String.valueOf(tarif.bmhp),
                            String.valueOf(tarif.sewaAlat),
                            tarif_poli_eks,
                            nama_dokter,
                            kode_tarif,
                            payor_id,
                            payor_cd,
                            cob_cd,
                            coder_nik,
                            "1", // status
                            now, // created_at
                            now // updated_at
                        }
                );

            } else {

                // ================= UPDATE =================
                Sequel.mengedittf(
                        "bridging_eklaim", "no_sep=?",
                        "no_kartu=?, tgl_masuk=?, tgl_pulang=?, cara_masuk=?, jenis_rawat=?, kelas_rawat=?, "
                        + "adl_sub_acute=?, adl_chronic=?, icu_indikator=?, icu_los=?, ventilator_hour=?, "
                        + "upgrade_class_ind=?, upgrade_class_class=?, upgrade_class_los=?, upgrade_class_payor=?, "
                        + "add_payment_pct=?, birth_weight=?, sistole=?, diastole=?, discharge_status=?, "
                        + "prosedur_non_bedah=?, prosedur_bedah=?, konsultasi=?, tenaga_ahli=?, keperawatan=?, "
                        + "penunjang=?, radiologi=?, laboratorium=?, pelayanan_darah=?, rehabilitasi=?, "
                        + "kamar=?, rawat_intensif=?, obat=?, obat_kronis=?, obat_kemoterapi=?, "
                        + "alkes=?, bmhp=?, sewa_alat=?, tarif_poli_eks=?, nama_dokter=?, "
                        + "kode_tarif=?, payor_id=?, payor_cd=?, cob_cd=?, coder_nik=?, status=?, updated_at=?",
                        48,
                        new String[]{
                            nomor_kartu, tgl_masuk, tgl_pulang,
                            cara_masuk, jenis_rawat, kelas_rawat,
                            adl_sub_acute, adl_chronic, icu_indikator, icu_los, ventilator_hour,
                            upgrade_class_ind, upgrade_class_class, upgrade_class_los, upgrade_class_payor,
                            add_payment_pct, birth_weight,
                            sistole, diastole, discharge_status,
                            String.valueOf(tarif.prosedurNonBedah),
                            String.valueOf(tarif.prosedurBedah),
                            String.valueOf(tarif.konsultasi),
                            String.valueOf(tarif.tenagaAhli),
                            String.valueOf(tarif.keperawatan),
                            "0",
                            String.valueOf(tarif.radiologi),
                            String.valueOf(tarif.laboratorium),
                            "0",
                            String.valueOf(tarif.rehabilitasi),
                            String.valueOf(tarif.kamar),
                            "0",
                            String.valueOf(tarif.obat),
                            String.valueOf(tarif.obatKronis),
                            String.valueOf(tarif.obatKemoterapi),
                            "0",
                            String.valueOf(tarif.bmhp),
                            String.valueOf(tarif.sewaAlat),
                            tarif_poli_eks,
                            nama_dokter,
                            kode_tarif,
                            payor_id,
                            payor_cd,
                            cob_cd,
                            coder_nik,
                            closed_billing ? "2" : "1",
                            now,
                            nomor_sep
                        }
                );
            }
        } catch (Exception e) {
            System.out.println("Gagal simpan Data Klaim ke DB: " + e.getMessage());
        }
    }

    private void saveIDRGToDB(String no_sep, String diagnosa, String prosedur) {
        try {
            diagnosa = (diagnosa == null || diagnosa.isEmpty()) ? "-" : diagnosa;
            prosedur = (prosedur == null || prosedur.isEmpty()) ? "-" : prosedur;

            String sql
                    = "INSERT INTO bridging_eklaim_idrg (no_sep, diagnosa, prosedur) "
                    + "VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "diagnosa = VALUES(diagnosa), "
                    + "prosedur = VALUES(prosedur)";
            Sequel.queryu2(sql,3, new String[]{no_sep, diagnosa, prosedur});

        } catch (Exception e) {
            System.out.println("Gagal simpan IDRG ke DB: " + e.getMessage());
        }
    }

    public static TarifRs hitungTarifRs(Connection con, String no_rawat) throws Exception {

        TarifRs t = new TarifRs();

        String sql
                = "SELECT "
                + "SUM(CASE WHEN status IN ('Ralan Dokter Paramedis','Ranap Dokter Paramedis') "
                + "AND nm_perawatan NOT LIKE '%terapi%' THEN totalbiaya ELSE 0 END) AS prosedur_non_bedah, "
                + "SUM(CASE WHEN status='Operasi' THEN totalbiaya ELSE 0 END) AS prosedur_bedah, "
                + "SUM(CASE WHEN status IN ('Ranap Dokter','Ralan Dokter') THEN totalbiaya ELSE 0 END) AS konsultasi, "
                + "SUM(CASE WHEN status IN ('Ranap Paramedis','Ralan Paramedis') THEN totalbiaya ELSE 0 END) AS keperawatan, "
                + "SUM(CASE WHEN status='Radiologi' THEN totalbiaya ELSE 0 END) AS radiologi, "
                + "SUM(CASE WHEN status='Laborat' THEN totalbiaya ELSE 0 END) AS laboratorium, "
                + "SUM(CASE WHEN status='Kamar' THEN totalbiaya ELSE 0 END) AS kamar, "
                + "SUM(CASE WHEN status IN ('Obat','Retur Obat','Resep Pulang') THEN totalbiaya ELSE 0 END) AS obat_total, "
                + "SUM(CASE WHEN status='Obat' AND nm_perawatan LIKE '%kronis%' THEN totalbiaya ELSE 0 END) AS obat_kronis, "
                + "SUM(CASE WHEN status='Obat' AND nm_perawatan LIKE '%kemo%' THEN totalbiaya ELSE 0 END) AS obat_kemo, "
                + "SUM(CASE WHEN status='Tambahan' THEN totalbiaya ELSE 0 END) AS bmhp, "
                + "SUM(CASE WHEN status IN ('Harian','Service') THEN totalbiaya ELSE 0 END) AS sewa_alat, "
                + "SUM(CASE WHEN status IN ('Ralan Dokter Paramedis','Ranap Dokter Paramedis') "
                + "AND nm_perawatan LIKE '%terapi%' THEN totalbiaya ELSE 0 END) AS rehabilitasi "
                + "FROM billing WHERE no_rawat=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, no_rawat);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                t.prosedurNonBedah = rs.getInt("prosedur_non_bedah");
                t.prosedurBedah = rs.getInt("prosedur_bedah");
                t.konsultasi = rs.getInt("konsultasi");
                t.keperawatan = rs.getInt("keperawatan");
                t.radiologi = rs.getInt("radiologi");
                t.laboratorium = rs.getInt("laboratorium");
                t.kamar = rs.getInt("kamar");

                int obatTotal = rs.getInt("obat_total");
                t.obatKronis = rs.getInt("obat_kronis");
                t.obatKemoterapi = rs.getInt("obat_kemo");
                t.obat = obatTotal - t.obatKronis - t.obatKemoterapi;

                t.bmhp = rs.getInt("bmhp");
                t.sewaAlat = rs.getInt("sewa_alat");
                t.rehabilitasi = rs.getInt("rehabilitasi");
            }
        }
        return t;
    }

    private static String CODERNIK() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = EnkripsiAES.decrypt(prop.getProperty("CODERNIK"));
        } catch (Exception e) {
            var = "3504042406990001";
        }
        return var;
    }
     
     private static String BRIDGINGEKLAIM() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("BRIDGINGEKLAIM");
        } catch (Exception e) {
            var = "no";
        }
        return var;
    }
}
