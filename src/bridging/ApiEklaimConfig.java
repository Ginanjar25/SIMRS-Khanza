package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import java.util.Base64;
import org.springframework.web.client.RestTemplate;

public class ApiEklaimConfig {        
    private String key,urlWS,urlauth,token, kelasRS;
    private long millis;
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private ApiEklaimAesKeySpec mykey;
    private HttpHeaders header ;
    private JsonNode root;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    
 public ApiEklaimConfig() {
        try {
            key = "8e96e280a56691be888656b7e945023c2f50ec5a09b38b0b96e7acf22d2f5582";
            urlWS = "http://192.168.106.100/E-Klaim/ws.php";
            kelasRS = "CS";
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
        }
    }

        
    public long GetUTCdatetimeAsString(){    
        millis = System.currentTimeMillis();   
        return millis/1000;
    }
   private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String Decrypt(String data, String key) throws Exception {
        // Remove the BEGIN and END headers from the encrypted data
        data = data.replace("----BEGIN ENCRYPTED DATA----", "")
               .replace("----END ENCRYPTED DATA----", "")
               .replaceAll("\\s+", ""); // Remove all whitespace characters
        
        data = data.replace("-", "+").replace("_", "/");
        // Decode the base64 encoded string
        byte[] decodedData = Base64.getDecoder().decode(data);

        // Convert the key to byte array
        byte[] keyBytes = hexStringToByteArray(key);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Needs a 256-bit key!");
        }

        // Split the decoded data into signature, iv, and encrypted data
        byte[] signature = Arrays.copyOfRange(decodedData, 0, 10);
        byte[] iv = Arrays.copyOfRange(decodedData, 10, 26);
        byte[] encrypted = Arrays.copyOfRange(decodedData, 26, decodedData.length);

        // Calculate the HMAC for the encrypted data
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
        sha256HMAC.init(keySpec);
        byte[] calcSignature = Arrays.copyOf(sha256HMAC.doFinal(encrypted), 10);

        // Compare the calculated signature with the received signature
        if (!MessageDigest.isEqual(signature, calcSignature)) {
            return "SIGNATURE_NOT_MATCH";
        }

        // Decrypt the data
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decryptedData = cipher.doFinal(encrypted);

        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    public String Encrypt(String data, String key) throws Exception {
        // Convert the key to byte array
        byte[] keyBytes = hexStringToByteArray(key);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Needs a 256-bit key!");
        }

        // Create initialization vector
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);

        // Encrypt the data
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Create signature
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
        sha256HMAC.init(keySpec);
        byte[] signature = Arrays.copyOf(sha256HMAC.doFinal(encrypted), 10);

        // Combine all, encode, and format
        byte[] encryptedMessage = new byte[signature.length + iv.length + encrypted.length];
        System.arraycopy(signature, 0, encryptedMessage, 0, signature.length);
        System.arraycopy(iv, 0, encryptedMessage, signature.length, iv.length);
        System.arraycopy(encrypted, 0, encryptedMessage, signature.length + iv.length, encrypted.length);

        String HasilEnk = Base64.getEncoder().encodeToString(encryptedMessage);
        return HasilEnk;
    }

    public String getKey() {
        return key;
    }
    
    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers= {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
            }
        };
        sslContext.init(null,trustManagers , new SecureRandom());
        sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme=new Scheme("https",443,sslFactory);
        factory=new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }

}
