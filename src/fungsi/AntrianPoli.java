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

    public void kirimAntrean() {
        try {
            String kd_dokter = akses.getkode();
            JSONObject jsonBody = new JSONObject();
            
            // Query untuk mengambil informasi dokter dan poli
            ps = koneksi.prepareStatement(
                "SELECT d.kd_dokter, d.nm_dokter, p.nm_poli, " +
                "concat(j.jam_mulai, ' - ', j.jam_selesai) AS jam_dokter " +
                "FROM dokter d " +
                "JOIN poliklinik p ON d.kd_poli = p.kd_poli " +
                "JOIN jadwal j ON d.kd_dokter = j.kd_dokter " +
                "WHERE d.kd_dokter = ? LIMIT 1"
            );
            ps.setString(1, kd_dokter);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                jsonBody.put("kode_dokter", rs.getString("kd_dokter"));
                jsonBody.put("nama_dokter", rs.getString("nm_dokter"));
                jsonBody.put("nama_poli", rs.getString("nm_poli"));
                jsonBody.put("jam_dokter", rs.getString("jam_dokter"));
            }
            
            // Query untuk mengambil data antrean
            JSONArray queueNumber = new JSONArray();
            JSONArray waitingList = new JSONArray();
            ps = koneksi.prepareStatement(
                "SELECT no_antrian, no_rawat " +
                "FROM antripoli " +
                "WHERE kd_dokter = ? AND status = '0' " +
                "ORDER BY no_antrian ASC LIMIT 6"
            );
            ps.setString(1, kd_dokter);
            rs = ps.executeQuery();

            int count = 0;
            while (rs.next()) {
                JSONObject antrean = new JSONObject();
                antrean.put("no_antrean", rs.getString("no_antrian"));
                antrean.put("no_rawat", rs.getString("no_rawat"));
                if (count == 0) {
                    queueNumber.put(antrean);
                } else {
                    waitingList.put(antrean);
                }
                count++;
            }
            jsonBody.put("queueNumber", queueNumber);
            jsonBody.put("waitingList", waitingList);

            // Kirim data ke API Bun.js
            String getIP = Sequel.cariIsi("select ruang_poli from side_db.set_ip_antrean where IP = ?", akses.getalamatip());
            URL url = new URL(link + "/" +getIP);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Mengubah JSON objek ke String dan mengirimkannya
            String jsonInputString = jsonBody.toString();
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Mendapatkan response dari server
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
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
