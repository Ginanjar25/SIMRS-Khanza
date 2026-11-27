/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import static com.sun.org.glassfish.external.amx.AMXUtil.prop;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import fungsi.sekuel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author khanzasoft
 */
public class AntrianPoli {
    public String link="";
    private static String var="";
    private sekuel Sequel=new sekuel();
    private Connection koneksi=koneksiDB.condb();
    private static final Properties prop = new Properties();
    private PreparedStatement ps;
    private ResultSet rs;
        
    public AntrianPoli(){
        try {
            link = URLAPIANTRIAN();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }
    }

public void kirimAntrean(String kd_poli, String kd_dokter, String no_reg, String no_rawat) {
    try {
        JSONObject jsonBody = new JSONObject();

        // Query to fetch doctor and schedule information
        ps = koneksi.prepareStatement(
            "SELECT d.kd_dokter, d.nm_dokter, p.nm_poli, " +
            "CONCAT(TIME_FORMAT(j.jam_mulai, '%H:%i'), ' - ',TIME_FORMAT(j.jam_selesai, '%H:%i')) AS jam_dokter, " +
            "DAYNAME(NOW()) AS hari, j.hari_kerja " +
            "FROM dokter d " +
            "INNER JOIN jadwal j ON j.kd_dokter = d.kd_dokter " +
            "INNER JOIN poliklinik p ON p.kd_poli = j.kd_poli " +
            "WHERE j.kd_dokter = ? AND j.kd_poli = ? " +
            "AND j.hari_kerja = CASE DAYNAME(NOW()) " +
            " WHEN 'Monday' THEN 'SENIN' " +
            " WHEN 'Tuesday' THEN 'SELASA' " +
            " WHEN 'Wednesday' THEN 'RABU' " +
            " WHEN 'Thursday' THEN 'KAMIS' " +
            " WHEN 'Friday' THEN 'JUMAT' " +
            " WHEN 'Saturday' THEN 'SABTU' " +
            " WHEN 'Sunday' THEN 'AKHAD' " +
            "END"
        );
        ps.setString(1, kd_dokter);
        ps.setString(2, kd_poli);
        rs = ps.executeQuery();

        if (rs.next()) {
            jsonBody.put("kode_dokter", rs.getString("kd_dokter"));
            jsonBody.put("nama_dokter", rs.getString("nm_dokter"));
            jsonBody.put("jam_dokter", rs.getString("jam_dokter"));
            jsonBody.put("nama_poli", rs.getString("nm_poli"));
        }

        JSONArray queueNumber = new JSONArray();
        JSONArray waitingList = new JSONArray();

        // Yang dipanggil = pasien yang dibuka dokter (no_rawat), BUKAN baris pertama antripoli.
        // Nomor = no_tiket_poli (Lembar 2 tiket anjungan, mis. SAR-B008) dari side_db.anjungan_antrean_poli.
        String nomorDipanggil = Sequel.cariIsi(
            "SELECT aap.no_tiket_poli FROM antripoli ap " +
            "INNER JOIN side_db.anjungan_antrean_poli aap ON aap.no_rawat = ap.no_rawat " +
            "WHERE ap.no_rawat = ?", no_rawat);
        if (nomorDipanggil.equals("")) {
            // Pasien tanpa tiket anjungan (daftar langsung di SIMRS): format lama poli_bpjs - no_antrian.
            nomorDipanggil = Sequel.cariIsi(
                "SELECT CONCAT(IFNULL(ap.poli_bpjs, ''), ' - ', ap.no_antrian) FROM antripoli ap WHERE ap.no_rawat = ?", no_rawat);
        }
        if (nomorDipanggil.equals("")) {
            nomorDipanggil = no_reg;
        }
        JSONObject dipanggil = new JSONObject();
        dipanggil.put("no_antrean", nomorDipanggil);
        dipanggil.put("no_rawat", no_rawat);
        queueNumber.put(dipanggil);

        // Daftar tunggu: pasien berikutnya selain yang sedang dipanggil.
        ps = koneksi.prepareStatement(
            "SELECT ap.no_rawat, aap.no_tiket_poli AS no_reg " +
            "FROM antripoli ap INNER JOIN reg_periksa rp ON rp.no_rawat = ap.no_rawat " +
            "INNER JOIN side_db.anjungan_antrean_poli aap ON aap.no_rawat = ap.no_rawat " +
            "WHERE ap.kd_dokter = ? " +
            "AND ap.kd_poli = ? AND ap.status != '2' " +
            "AND DATE(ap.created_at) = DATE(NOW()) " +
            "AND rp.stts = 'Belum' AND ap.no_rawat <> ? " +
            "ORDER BY ap.no_antrian ASC LIMIT 3"
        );
        ps.setString(1, kd_dokter);
        ps.setString(2, kd_poli);
        ps.setString(3, no_rawat);
        rs = ps.executeQuery();

        while (rs.next()) {
            JSONObject antrean = new JSONObject();
            antrean.put("no_antrean", rs.getString("no_reg"));
            antrean.put("no_rawat", rs.getString("no_rawat"));
            waitingList.put(antrean);
        }

        // Fill waitingList with "000" placeholders until it has 6 items
        while (waitingList.length() < 3) {
            JSONObject emptyAntrean = new JSONObject();
            emptyAntrean.put("no_antrean", "000");
            emptyAntrean.put("no_rawat", "000");
            waitingList.put(emptyAntrean);
        }

        jsonBody.put("queueNumber", queueNumber);
        jsonBody.put("waitingList", waitingList);

        String getIP = Sequel.cariIsi("select ruang_poli from side_db.set_ip_antrean where ip_address = ?", akses.getalamatip());
        if (getIP.equals("") || link.equals("")) {
            return; // IP PC ini belum terdaftar di side_db.set_ip_antrean / URLAPIANTRIAN kosong
        }
        URL url = new URL(link + "/poli/" + getIP);
        kirimPOST(url, jsonBody.toString());
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

public void kirimAntrianAwal(String kd_poli, String kd_dokter) {
    try {
        JSONObject jsonBody = new JSONObject();

        // Query to fetch doctor and schedule information
        ps = koneksi.prepareStatement(
            "SELECT d.kd_dokter, d.nm_dokter, p.nm_poli, " +
            "CONCAT (TIME_FORMAT(j.jam_mulai, '%H:%i'), ' - ',TIME_FORMAT(j.jam_selesai, '%H:%i')) AS jam_dokter, " +
            "DAYNAME(NOW()) AS hari, j.hari_kerja " +
            "FROM dokter d " +
            "INNER JOIN jadwal j ON j.kd_dokter = d.kd_dokter " +
            "INNER JOIN poliklinik p ON p.kd_poli = j.kd_poli " +
            "WHERE j.kd_dokter = ? AND j.kd_poli = ? " +
            "AND j.hari_kerja = CASE DAYNAME(NOW()) " +
            " WHEN 'Monday' THEN 'SENIN' " +
            " WHEN 'Tuesday' THEN 'SELASA' " +
            " WHEN 'Wednesday' THEN 'RABU' " +
            " WHEN 'Thursday' THEN 'KAMIS' " +
            " WHEN 'Friday' THEN 'JUMAT' " +
            " WHEN 'Saturday' THEN 'SABTU' " +
            " WHEN 'Sunday' THEN 'AKHAD' " +
            "END"
        );
        ps.setString(1, kd_dokter);
        ps.setString(2, kd_poli);
        rs = ps.executeQuery();

        if (rs.next()) {
            jsonBody.put("kode_dokter", rs.getString("kd_dokter"));
            jsonBody.put("nama_dokter", rs.getString("nm_dokter"));
            jsonBody.put("jam_dokter", rs.getString("jam_dokter"));
            jsonBody.put("nama_poli", rs.getString("nm_poli"));
        }

        JSONArray queueNumber = new JSONArray();
        JSONArray waitingList = new JSONArray();
        ps = koneksi.prepareStatement(
            "SELECT ap.kd_dokter, ap.kd_poli, ap.status, ap.no_rawat, ap.no_antrian, " +
            "DATE(NOW()) AS tanggal, aap.no_tiket_poli AS no_reg, " +
            "ap.created_at, ap.updated_at, rp.stts " +
            "FROM antripoli ap INNER JOIN reg_periksa rp ON rp.no_rawat = ap.no_rawat " +
            "INNER JOIN side_db.anjungan_antrean_poli aap ON aap.no_rawat = ap.no_rawat " +
            "WHERE ap.kd_dokter = ? " +
            "AND ap.kd_poli = ? AND ap.status != '2' " +
            "AND DATE(ap.created_at) = DATE(NOW()) " +
            "AND rp.stts = 'Belum' " +
            "ORDER BY ap.no_antrian ASC LIMIT 4;"
        );
        ps.setString(1, kd_dokter);
        ps.setString(2, kd_poli);
        rs = ps.executeQuery();

        JSONObject antrean1 = new JSONObject();
        antrean1.put("no_antrean", "000");
        antrean1.put("no_rawat", "000");
        queueNumber.put(antrean1);
        int count = 0;
        while (rs.next()) {
            JSONObject antrean = new JSONObject();
            antrean.put("no_antrean", rs.getString("no_reg"));
            antrean.put("no_rawat", rs.getString("no_rawat"));
            waitingList.put(antrean);
            count++;
        }

        // Fill waitingList with "000" placeholders until it has 6 items
        while (waitingList.length() < 3) {
            JSONObject emptyAntrean = new JSONObject();
            emptyAntrean.put("no_antrean", "000");
            emptyAntrean.put("no_rawat", "000");
            waitingList.put(emptyAntrean);
        }

        jsonBody.put("queueNumber", queueNumber);
        jsonBody.put("waitingList", waitingList);

        String getIP = Sequel.cariIsi("select ruang_poli from side_db.set_ip_antrean where ip_address = ?", akses.getalamatip());
        if (getIP.equals("") || link.equals("")) {
            return; // IP PC ini belum terdaftar di side_db.set_ip_antrean / URLAPIANTRIAN kosong
        }
        URL url = new URL(link + "/poli/" + getIP);
        kirimPOST(url, jsonBody.toString());
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

public void kirimAntrianIGD(String kd_poli, String kd_dokter) {
    String jamDokter = "";
    try {
        JSONObject jsonBody = new JSONObject();
        // Query to fetch doctor and schedule information
        ps = koneksi.prepareStatement(
            "SELECT d.kd_dokter, d.nm_dokter,  CURRENT_TIME() AS JamSekarang\n" +
            "FROM dokter d \n" +
            "WHERE d.kd_dokter = ? LIMIT 1"
        );
        ps.setString(1, kd_dokter);
        rs = ps.executeQuery();

        if (rs.next()) {
            String jamNowStr = rs.getString("JamSekarang"); // Mendapatkan waktu sebagai string
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime jamNow = LocalTime.parse(jamNowStr, timeFormatter); // Konversi ke LocalTime

            // Definisikan rentang waktu
            LocalTime pagi = LocalTime.parse("07:00:00", timeFormatter);
            LocalTime siang = LocalTime.parse("14:00:00", timeFormatter);
            LocalTime malam = LocalTime.parse("21:00:00", timeFormatter);

            if (jamNow.isAfter(pagi) && jamNow.isBefore(siang)) {
                jamDokter = "07:00 - 14:00";
            } else if (jamNow.isAfter(siang) && jamNow.isBefore(malam)) {
                jamDokter = "14:00 - 21:00";
            } else if (jamNow.isAfter(malam) && jamNow.isBefore(pagi)) {
                jamDokter = "21:00 - 07:00";
            }
            jsonBody.put("kode_dokter", rs.getString("kd_dokter"));
            jsonBody.put("nama_dokter", rs.getString("nm_dokter"));
            jsonBody.put("jam_dokter", jamDokter);
            jsonBody.put("nama_poli", "IGD");
        }

        ps = koneksi.prepareStatement(
                "SELECT\n" +
                "    SUM(CASE WHEN igdsek.plan = 'Zona Hijau' THEN 1 ELSE 0 END) AS hijau,\n" +
                "    SUM(CASE WHEN igdsek.plan = 'Zona Kuning' THEN 1 ELSE 0 END) AS kuning,\n" +
                "    SUM(CASE WHEN igdpri.plan IN ('Ruang Kritis', 'Ruang Resusitasi') THEN 1 ELSE 0 END) AS merah,\n" +
                "    reg.no_rawat\n" +
                "FROM (\n" +
                "    SELECT * FROM reg_periksa\n" +
                "    WHERE kd_poli = 'IGDK'\n" +
                "    AND status_lanjut = 'ralan'\n" +
                "    AND status_bayar = 'Belum Bayar'\n" +
                "    AND tgl_registrasi BETWEEN CURDATE() - INTERVAL 1 DAY AND CURDATE()\n" +
                ") AS reg\n" +
                "LEFT JOIN data_triase_igdsekunder igdsek ON igdsek.no_rawat = reg.no_rawat\n" +
                "LEFT JOIN data_triase_igdprimer igdpri ON igdpri.no_rawat = reg.no_rawat"
        );
        rs = ps.executeQuery();
        if (rs.next()) {
            jsonBody.put("green_zone", rs.getString("hijau"));
            jsonBody.put("yellow_zone", rs.getString("kuning"));
            jsonBody.put("red_zone", rs.getString("merah"));
        }
        
        
        JSONArray perawat = new JSONArray();
        for (int i = 0; i < 3; i++) {
            JSONObject emptyPerawat = new JSONObject();
            emptyPerawat.put("nik", "");
            emptyPerawat.put("nama", "");
            perawat.put(emptyPerawat);
        }
        jsonBody.put("perawat", perawat);
        URL url = new URL(link + "/igd");
        kirimPOST(url, jsonBody.toString());
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

/**
 * Kirim perawat jaga + jumlah pasien per zona ke display IGD (POST {URLAPIANTRIAN}/igd).
 * nik[] dan nama[] berpasangan sesuai urutan kolom Petugas di Set Display IGD (saat ini 4 perawat);
 * kolom kosong dikirim dengan nik "placeholder" dan nama kosong.
 */
public void kirimAntrianPerawatIGD(String[] nik, String[] nama) {
    try {
        JSONObject jsonBody = new JSONObject();
        ps = koneksi.prepareStatement(
                "SELECT\n" +
                "    SUM(CASE WHEN igdsek.plan = 'Zona Hijau' THEN 1 ELSE 0 END) AS hijau,\n" +
                "    SUM(CASE WHEN igdsek.plan = 'Zona Kuning' THEN 1 ELSE 0 END) AS kuning,\n" +
                "    SUM(CASE WHEN igdpri.plan IN ('Ruang Kritis', 'Ruang Resusitasi') THEN 1 ELSE 0 END) AS merah,\n" +
                "    reg.no_rawat\n" +
                "FROM (\n" +
                "    SELECT * FROM reg_periksa\n" +
                "    WHERE kd_poli = 'IGDK'\n" +
                "    AND status_lanjut = 'ralan'\n" +
                "    AND status_bayar = 'Belum Bayar'\n" +
                "    AND tgl_registrasi BETWEEN CURDATE() - INTERVAL 1 DAY AND CURDATE()\n" +
                ") AS reg\n" +
                "LEFT JOIN data_triase_igdsekunder igdsek ON igdsek.no_rawat = reg.no_rawat\n" +
                "LEFT JOIN data_triase_igdprimer igdpri ON igdpri.no_rawat = reg.no_rawat"
        );
        rs = ps.executeQuery();
        if (rs.next()) {
            jsonBody.put("kode_dokter", "");
            jsonBody.put("nama_dokter", "");
            jsonBody.put("jam_dokter", "");
            jsonBody.put("nama_poli", "IGD");
            jsonBody.put("green_zone", rs.getString("hijau"));
            jsonBody.put("yellow_zone", rs.getString("kuning"));
            jsonBody.put("red_zone", rs.getString("merah"));
        }
        JSONArray perawat = new JSONArray();
        for (int i = 0; i < nik.length; i++) {
            JSONObject perawatObj = new JSONObject();
            perawatObj.put("nik", (nik[i] == null || nik[i].isEmpty()) ? "placeholder" : nik[i]);
            perawatObj.put("nama", (i < nama.length && nama[i] != null) ? nama[i] : "");
            perawat.put(perawatObj);
        }
        jsonBody.put("perawat", perawat);

        if (link.equals("")) {
            javax.swing.JOptionPane.showMessageDialog(null, "URLAPIANTRIAN di setting/database.xml belum diisi.");
            return;
        }
        URL url = new URL(link + "/igd");
        kirimPOST(url, jsonBody.toString(), "Perawat jaga terkirim ke display IGD.");
    } catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Gagal menyiapkan data display IGD: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

    // ---- Tutup / buka antrean dokter hari ini (perawat rawat jalan) ----
    // Disimpan di side_db.anjungan_tutup_antrean (satu baris = antrean dokter + poli ditutup pada tanggal itu;
    // besok otomatis buka lagi). Anjungan membaca tabel ini: kartu dokter hilang dari layar display FO dan
    // dokter tidak bisa dipilih di menu Daftar Onsite. Setelah itu anjungan dikabari (POST /callQueue/tutup)
    // agar layar FO langsung memuat ulang; bila anjungan tidak bisa dihubungi, layar FO tetap berubah
    // paling lambat 1 menit kemudian.
    private static final String DDL_TUTUP_ANTREAN =
        "CREATE TABLE IF NOT EXISTS side_db.anjungan_tutup_antrean (" +
        " tanggal DATE NOT NULL," +
        " kd_poli VARCHAR(5) NOT NULL," +
        " kd_dokter VARCHAR(20) NOT NULL," +
        " ditutup_oleh VARCHAR(30) NOT NULL DEFAULT ''," +
        " jam_tutup DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
        " PRIMARY KEY (tanggal, kd_poli, kd_dokter)" +
        ") ENGINE=InnoDB";

    // tutupBukaAntrean: bila antrean dokter + poli hari ini masih buka → konfirmasi lalu tutup; bila sudah
    // ditutup → konfirmasi lalu buka kembali.
    public void tutupBukaAntrean(String kd_poli, String kd_dokter, String nm_poli, String nm_dokter) {
        if (kd_poli == null || kd_poli.trim().isEmpty() || kd_dokter == null || kd_dokter.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Pilih dulu pasien pada tabel (poli & dokter belum terbaca).");
            return;
        }
        kd_poli = kd_poli.trim();
        kd_dokter = kd_dokter.trim();
        String siapa = nm_dokter + " (" + nm_poli + ")";
        try {
            try (java.sql.Statement st = koneksi.createStatement()) {
                st.execute(DDL_TUTUP_ANTREAN);
            }
            String jam = "", oleh = "";
            boolean ditutup = false;
            try (PreparedStatement p = koneksi.prepareStatement(
                    "SELECT TIME_FORMAT(jam_tutup, '%H:%i'), ditutup_oleh FROM side_db.anjungan_tutup_antrean " +
                    "WHERE tanggal = CURDATE() AND kd_poli = ? AND kd_dokter = ?")) {
                p.setString(1, kd_poli);
                p.setString(2, kd_dokter);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) {
                        ditutup = true;
                        jam = r.getString(1);
                        oleh = r.getString(2);
                    }
                }
            }

            if (!ditutup) {
                int jawab = javax.swing.JOptionPane.showConfirmDialog(null,
                    "Tutup antrean " + siapa + " hari ini?\n\n" +
                    "- Kartu dokter ini hilang dari layar display FO.\n" +
                    "- Dokter ini tidak bisa dipilih lagi di menu Daftar Onsite anjungan.\n\n" +
                    "Bisa dibuka kembali lewat menu yang sama.",
                    "Tutup Antrean Dokter", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
                if (jawab != javax.swing.JOptionPane.YES_OPTION) return;
            } else {
                int jawab = javax.swing.JOptionPane.showConfirmDialog(null,
                    "Antrean " + siapa + " sudah DITUTUP pukul " + jam + (oleh.isEmpty() ? "" : " oleh " + oleh) + ".\n\n" +
                    "Buka kembali antrean dokter ini?",
                    "Buka Kembali Antrean Dokter", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                if (jawab != javax.swing.JOptionPane.YES_OPTION) return;
            }

            boolean tutup = !ditutup;
            setAntreanTutup(kd_poli, kd_dokter, tutup);
            javax.swing.JOptionPane.showMessageDialog(null, "Antrean " + siapa + (tutup ? " DITUTUP." : " DIBUKA kembali."));
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Gagal mengubah status antrean dokter: " + e.getMessage());
        }
    }

    // setAntreanTutup: tutup (tutup = true) atau buka kembali antrean dokter + poli hari ini, tanpa dialog.
    // Status tersimpan di database; kabar ke anjungan hanya mempercepat layar FO (gagal = diam).
    public void setAntreanTutup(String kd_poli, String kd_dokter, boolean tutup) throws Exception {
        try (java.sql.Statement st = koneksi.createStatement()) {
            st.execute(DDL_TUTUP_ANTREAN);
        }
        String sql = tutup
            ? "INSERT IGNORE INTO side_db.anjungan_tutup_antrean (kd_poli, kd_dokter, ditutup_oleh, tanggal, jam_tutup) VALUES (?, ?, ?, CURDATE(), NOW())"
            : "DELETE FROM side_db.anjungan_tutup_antrean WHERE kd_poli = ? AND kd_dokter = ? AND tanggal = CURDATE()";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, kd_poli.trim());
            p.setString(2, kd_dokter.trim());
            if (tutup) p.setString(3, akses.getkode());
            p.executeUpdate();
        }
        if (link != null && !link.trim().isEmpty()) {
            JSONObject body = new JSONObject();
            body.put("kd_poli", kd_poli.trim());
            body.put("kd_dokter", kd_dokter.trim());
            body.put("tutup", tutup);
            kirimPOST(new URL(link + "/tutup"), body.toString());
        }
    }

    // daftarAntreanHariIni: dokter + poli yang praktik hari ini beserta status antreannya, diambil dari
    // anjungan (GET {URLAPIANTRIAN}/tutup). Isi tiap elemen: kd_poli, nm_poli, kd_dokter, nm_dokter,
    // jam_mulai, jam_selesai, pasien, jadwal_buka, ditutup, ditutup_oleh, jam_tutup.
    public JSONArray daftarAntreanHariIni() throws Exception {
        if (link == null || link.trim().isEmpty()) {
            throw new Exception("URLAPIANTRIAN di setting/database.xml belum diisi.");
        }
        HttpURLConnection c = (HttpURLConnection) new URL(link + "/tutup").openConnection();
        try {
            c.setConnectTimeout(3000);
            c.setReadTimeout(5000);
            c.setRequestMethod("GET");
            int kode = c.getResponseCode();
            java.io.InputStream in = kode < 300 ? c.getInputStream() : c.getErrorStream();
            String isi = "";
            if (in != null) {
                try (java.util.Scanner sc = new java.util.Scanner(in, "UTF-8").useDelimiter("\\A")) {
                    isi = sc.hasNext() ? sc.next() : "";
                }
            }
            if (kode >= 300) {
                throw new Exception("anjungan menjawab HTTP " + kode + ": " + isi);
            }
            return new JSONObject(isi).getJSONArray("dokter");
        } finally {
            c.disconnect();
        }
    }

    // kirimPOST mengirim JSON ke server antrean di thread terpisah agar layar Khanza tidak macet
    // (hingga 3 + 3 detik) saat server antrean mati / lambat. Kegagalan hanya dicatat di konsol.
    private static void kirimPOST(final URL url, final String body) {
        kirimPOST(url, body, null);
    }

    // kirimPOST dengan pesan hasil ke petugas (pesanSukses != null): muncul dialog berhasil / gagal.
    private static void kirimPOST(final URL url, final String body, final String pesanSukses) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String gagal = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = body.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    int kode = connection.getResponseCode();
                    if (kode >= 300) {
                        gagal = "server antrean menjawab HTTP " + kode;
                    }
                } catch (Exception e) {
                    gagal = "server antrean tidak bisa dihubungi (" + e.getMessage() + ")";
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                if (gagal != null) {
                    System.out.println("Antrean " + url + " : " + gagal);
                }
                if (pesanSukses != null) {
                    final String hasil = gagal;
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (hasil == null) {
                                javax.swing.JOptionPane.showMessageDialog(null, pesanSukses);
                            } else {
                                javax.swing.JOptionPane.showMessageDialog(null,
                                    "Gagal mengirim ke display antrean:\n" + hasil + "\n\nAlamat: " + url,
                                    "Display antrean", javax.swing.JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                }
            }
        }, "kirim-antrean").start();
    }

    public static String URLAPIANTRIAN(){
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIANTRIAN");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }
    
}
