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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author khanzasoft
 */
public class APIInternalRSPW {

    public String link = "", copies = "1";
    private static String var = "";
    private Connection koneksi = koneksiDB.condb();
    private static final Properties prop = new Properties();
    private PreparedStatement ps;
    private ResultSet rs;
    private sekuel Sequel = new sekuel();

    public APIInternalRSPW() {
        try {
            link = URLPRINTERRAJAL();
            copies = COPIESBARCODERM();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }
    }

    public void cetakBarcodeRajal(String no_rawat) {
        try {
            JSONObject jsonBody = new JSONObject();

            // Query to fetch doctor and schedule information
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,pasien.no_ktp,\n"
                    + "reg_periksa.kd_dokter,dokter.nm_dokter,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,\n"
                    + "concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,poliklinik.nm_poli,date_format(pasien.tgl_lahir,'%d-%m-%Y') as tgl_lahir,\n"
                    + "reg_periksa.p_jawab,reg_periksa.almt_pj,reg_periksa.hubunganpj,reg_periksa.biaya_reg,\n"
                    + "reg_periksa.stts_daftar,penjab.png_jawab,pasien.no_tlp,reg_periksa.stts,reg_periksa.status_poli, \n"
                    + "reg_periksa.kd_poli,reg_periksa.kd_pj,IFNULL(concat(bsep.no_sep),if(reg_periksa.kd_pj='BPJ','-','')) AS sep, IFNULL(bsep.klsrawat,'') AS kls from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter \n"
                    + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis\n"
                    + "left join bridging_sep bsep ON bsep.no_rawat = reg_periksa.no_rawat\n"
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli inner join penjab on reg_periksa.kd_pj=penjab.kd_pj where reg_periksa.no_rawat= ?"
            );
            try {
                ps.setString(1, no_rawat);
                rs = ps.executeQuery();

                if (rs.next()) {
                    jsonBody.put("rm", rs.getString("no_rkm_medis"));
                    jsonBody.put("nik", rs.getString("no_ktp"));
                    jsonBody.put("tanggal", rs.getString("tgl_registrasi"));
                    jsonBody.put("jam", rs.getString("jam_reg"));
                    jsonBody.put("nama", rs.getString("nm_pasien"));
                    jsonBody.put("tgl_lahir", rs.getString("tgl_lahir"));
                    jsonBody.put("umur", rs.getString("umur"));
                    jsonBody.put("jk", rs.getString("jk"));

                    jsonBody.put("alamat", rs.getString("almt_pj"));
                    jsonBody.put("poli", rs.getString("nm_poli"));
                    jsonBody.put("dokter", rs.getString("nm_dokter"));
                    jsonBody.put("cara_bayar", rs.getString("png_jawab") + " " + rs.getString("kls"));
                    jsonBody.put("no_sep", rs.getString("sep"));
                    jsonBody.put("copies", copies);
                }
                URL url = new URL(link);
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
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line.trim());
                        }
                    }
                    // Parse JSON
                    JSONObject json = new JSONObject(response.toString());
                    if (json.has("success") && json.getBoolean("success")) {
                        // Simpan ke DB
                        if (!Sequel.cariIsi("select status from antripoli where no_rawat=?", no_rawat).equals("2")) {
                            Sequel.mengedit("antripoli", "no_rawat = ?", "status=?, updated_at = now() ", 2, new String[]{"1", no_rawat});
                        }

                    }
                } else {
                    System.out.println("Gagal mengirim request. Kode HTTP: " + responseCode);
                }
            } catch (Exception e) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String URLPRINTERRAJAL() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIPRINTERRAJAL");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

    public static String COPIESBARCODERM() {
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("COPIESBARCODERM");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }

}
