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

/**
 *
 * @author khanzasoft
 */
public class AntrianPoli {
    public String link="";
    private static String var="";
    private sekuel Sequel=new sekuel();
    private static final Properties prop = new Properties();
        
    public AntrianPoli(){
        super();
        try {
            link=URLAPIANTRIAN();  
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
    }
    
    public void kirimAntrean(String nik) {
        try {
            // URL of the Bun.js backend
            
            String getIP = Sequel.cariIsi("select ruang_poli from side_db.set_ip_antrean where IP = ?", akses.getalamatip());
            URL url = new URL(link + "/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // The request payload
            String jsonInputString = "{\"queueNumber\": \"A042\"}";

            // Write the request to the connection output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String URLAPIANTRIAN(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLAPIANTRIAN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
}
