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

public void kirimAntrean(String kd_poli, String kd_dokter) {
    try {
        JSONObject jsonBody = new JSONObject();

        // Query to fetch doctor and schedule information
        ps = koneksi.prepareStatement(
            "SELECT d.kd_dokter, d.nm_dokter, p.nm_poli, " +
            "concat(j.jam_mulai, ' - ', j.jam_selesai) AS jam_dokter, DAYNAME(NOW()) AS hari, j.hari_kerja " +
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
            "AND ap.kd_poli = ? AND ap.status = '0' " +
            "AND DATE(ap.created_at) = DATE(NOW()) " +
            "AND rp.stts != 'Sudah' " +
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
        URL url = new URL(link + "/" + getIP);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = jsonBody.toString();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

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
            "concat(j.jam_mulai, ' - ', j.jam_selesai) AS jam_dokter, DAYNAME(NOW()) AS hari, j.hari_kerja " +
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
            "AND ap.kd_poli = ? AND ap.status = '0' " +
            "AND DATE(ap.created_at) = DATE(NOW()) " +
            "AND rp.stts != 'Sudah' " +
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
        URL url = new URL(link + "/" + getIP);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = jsonBody.toString();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

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
