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
    String kd_petugas = "placeholder", kd_petugas2 ="placeholder", kd_petugas3="placeholder", nm_petugas="", nm_petugas2="", nm_petugas3="";
        
    public AntrianPoli(){
        try {
            link = URLAPIANTRIAN();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }
    }

public void kirimAntrean(String kd_poli, String kd_dokter) {
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
        ps = koneksi.prepareStatement(
            "SELECT ap.kd_dokter, ap.kd_poli, ap.status, ap.no_rawat, ap.no_antrian, " +
            "DATE(NOW()) AS tanggal, CONCAT(ap.poli_bpjs, ' - ', ap.no_antrian) AS no_reg, " +
            "ap.created_at, ap.updated_at, rp.stts " +
            "FROM antripoli ap INNER JOIN reg_periksa rp ON rp.no_rawat = ap.no_rawat " +
            "WHERE ap.kd_dokter = ? " +
            "AND ap.kd_poli = ? AND ap.status != '2' " +
            "AND DATE(ap.created_at) = DATE(NOW()) " +
            "AND rp.stts = 'Belum' " +
            "ORDER BY ap.no_antrian ASC LIMIT 4;"
        );
        ps.setString(1, kd_dokter);
        ps.setString(2, kd_poli);
        rs = ps.executeQuery();

        int count = 0;
        while (rs.next()) {
            JSONObject antrean = new JSONObject();
            antrean.put("no_antrean", rs.getString("no_reg"));
            antrean.put("no_rawat", rs.getString("no_rawat"));
            if (count == 0) {
                queueNumber.put(antrean);
            } else {
                waitingList.put(antrean);
            }
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
        URL url = new URL(link + "/poli/" + getIP);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3000); // Timeout koneksi 2 detik
        connection.setReadTimeout(3000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = jsonBody.toString();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        connection.getResponseCode();
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
            "DATE(NOW()) AS tanggal, CONCAT(ap.poli_bpjs, ' - ', ap.no_antrian) AS no_reg, " +
            "ap.created_at, ap.updated_at, rp.stts " +
            "FROM antripoli ap INNER JOIN reg_periksa rp ON rp.no_rawat = ap.no_rawat " +
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
        URL url = new URL(link + "/poli/" + getIP);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3000); // Timeout koneksi 2 detik
        connection.setReadTimeout(3000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = jsonBody.toString();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        connection.getResponseCode();
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
            "SELECT d.kd_dokter, d.nm_dokter, p.nm_poli, CURRENT_TIME() AS JamSekarang\n" +
            "FROM dokter d \n" +
            "LEFT JOIN jadwal j ON j.kd_dokter = d.kd_dokter \n" +
            "INNER JOIN poliklinik p ON p.kd_poli = j.kd_poli \n" +
            "WHERE j.kd_dokter = ? AND j.kd_poli = ? LIMIT 1"
        );
        ps.setString(1, kd_dokter);
        ps.setString(2, kd_poli);
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
            jsonBody.put("nama_poli", rs.getString("nm_poli"));
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
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3000); // Timeout koneksi 2 detik
        connection.setReadTimeout(3000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = jsonBody.toString();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        connection.getResponseCode();
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

public void kirimAntrianPerawatIGD(String nik1, String nama1, String nik2, String nama2, String nik3, String nama3) {
    try {
        kd_petugas = (nik1 == null || nik1.isEmpty()) ? "placeholder" : nik1;
        kd_petugas2 = (nik2 == null || nik2.isEmpty()) ? "placeholder" : nik2;
        kd_petugas3 = (nik3 == null || nik3.isEmpty()) ? "placeholder" : nik3;
        nm_petugas = nama1;
        nm_petugas2 = nama2;
        nm_petugas3 = nama3;
        
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
        String[] nikArray = {kd_petugas, kd_petugas2, kd_petugas3};
        String[] namaArray = {nm_petugas, nm_petugas2, nm_petugas3};

        for (int i = 0; i < 3; i++) {
            JSONObject perawatObj = new JSONObject();
            perawatObj.put("nik", nikArray[i]);
            perawatObj.put("nama", namaArray[i]);
            perawat.put(perawatObj);
        }
        jsonBody.put("perawat", perawat);

        URL url = new URL(link + "/igd");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3000); // Timeout koneksi 2 detik
        connection.setReadTimeout(3000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = jsonBody.toString();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        connection.getResponseCode();
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
