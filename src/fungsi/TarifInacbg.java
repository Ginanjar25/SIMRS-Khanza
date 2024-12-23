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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author khanzasoft
 */
public class TarifInacbg {
    public String link="";
    private static String var="";
    private sekuel Sequel=new sekuel();
    private Connection koneksi=koneksiDB.condb();
    private static final Properties prop = new Properties();
    private PreparedStatement ps;
    private ResultSet rs;
        
    public TarifInacbg(){
        try {
            link = URLAPIGROUPINGINACBG();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }
    }

public void kirimICDGrouping(String no_rawat, String diagnosa, String prosedur) {
    try {
        JSONObject jsonBody = new JSONObject();
        String no_sep = Sequel.cariIsi("select no_sep from bridging_sep where no_rawat = ?", no_rawat);

        if (!no_sep.isBlank() && !no_sep.isEmpty()) {
            jsonBody.put("no_sep", no_sep);
            jsonBody.put("diagnosa", diagnosa);
            jsonBody.put("prosedur", prosedur);

            URL url = new URL(link + "/inacbg/UpdateGrouping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Kirim data ke server
            String jsonInputString = jsonBody.toString();
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Baca respons dari server
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            // Parsing JSON respons
            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
            int status = jsonResponse.getInt("status");
            if (status == 200) {
                JSONObject body = jsonResponse.getJSONObject("body"); // Akses 'body'
                JSONArray messageArray = body.getJSONArray("message"); // Akses 'message' dari 'body'

                if (messageArray.length() > 0) {
                    // Data utama
                    JSONObject data = messageArray.getJSONObject(0);
                    String code_cbg = data.getString("cbgCode");
                    String deskripsi = data.getString("deskripsi");
                    Integer tarif = data.getInt("tarif");
                    Integer tarif_naik = 0;
                    Integer tarif_total = 0;
                    // Informasi tambahan (jika ada)
                    if (messageArray.length() > 1) {
                        JSONObject tarifInfo = messageArray.getJSONObject(1);
                        tarif_naik = tarifInfo.getInt("tarifNaik");
                        tarif_total = tarifInfo.getInt("tarifTotal");
                    }
                    try {
                        simpanGrouping(code_cbg, tarif, tarif_naik, diagnosa, prosedur, no_sep, deskripsi);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            } else {
                System.err.println("Error: " + jsonResponse.getJSONObject("body").getString("error"));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void simpanGrouping(String kode_inacbg, Integer tarif, Integer tarifNaik, String diagnosa, String prosedur, String no_sep, String deskripsi) {
    try {
        JSONObject jsonBody = new JSONObject();
        
        if (!no_sep.isBlank() && !no_sep.isEmpty()) {
            jsonBody.put("kode_inacbg", kode_inacbg);
            jsonBody.put("tarif", tarif);
            jsonBody.put("tarifNaik", tarifNaik);
            jsonBody.put("diagnosa", diagnosa);
            jsonBody.put("prosedur", prosedur);
            jsonBody.put("no_sep", no_sep);
            jsonBody.put("deskripsi", deskripsi);

            URL url = new URL(link + "/inacbg/saveINACBG");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Kirim data ke server
            String jsonInputString = jsonBody.toString();
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Baca respons dari server
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            // Parsing JSON respons
            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
            int status = jsonResponse.getInt("status");
            if (status == 200) {
                JSONObject body = jsonResponse.getJSONObject("body"); // Akses 'body'
                if (body.length() > 0) {
                    System.out.println("Message : " + body.getString("message"));
                }
            } else {
                System.err.println("Error: " + jsonResponse.getJSONObject("body").getString("error"));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public String getNaikKelas(String no_sep) {
    String naik_kelas = Sequel.cariIsi("SELECT klsrawat FROM bridging_sep WHERE no_sep = ?", no_sep);
    
    Map<String, String> kelasMap = new HashMap<>();
    kelasMap.put("1", "vvip");
    kelasMap.put("2", "vip");
    kelasMap.put("3", "kelas_1");
    kelasMap.put("4", "kelas_2");
    kelasMap.put("8", "vip");

    return kelasMap.getOrDefault(naik_kelas, "");
}


    public static String URLAPIGROUPINGINACBG(){
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIGROUPINGINACBG");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }
    
}
