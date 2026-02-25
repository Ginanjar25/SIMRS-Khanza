package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class ApiApotekBPJS {        
    private String Key,Consid, lastResponse = "";;
    private String salt;
    private String generateHmacSHA256Signature;
    private byte[] hmacData;
    private Mac mac;
    private long millis;
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private SecretKeySpec secretKey;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private ApiBPJSAesKeySpec mykey;
    private sekuel Sequel=new sekuel();
    
    public ApiApotekBPJS(){
        try {
            Key = koneksiDB.SECRETKEYAPIAPOTEKBPJS();
            Consid = koneksiDB.CONSIDAPIAPOTEKBPJS();
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
    }

    public String getHmac(String utc) {               
        salt = Consid +"&"+utc;
	generateHmacSHA256Signature = null;
	try {
	    generateHmacSHA256Signature = generateHmacSHA256Signature(salt,Key);
	} catch (GeneralSecurityException e) {
	    // TODO Auto-generated catch block
            System.out.println("Error Signature : "+e);
	    e.printStackTrace();
	}
	return generateHmacSHA256Signature;
    }
    
    public String generateHmacSHA256Signature(String data, String key)throws GeneralSecurityException {
        hmacData = null;
	try {
            secretKey = new SecretKeySpec(key.getBytes("UTF-8"),"HmacSHA256");
	    mac = Mac.getInstance("HmacSHA256");
	    mac.init(secretKey);
	    hmacData = mac.doFinal(data.getBytes("UTF-8"));
	    return new String(Base64.encode(hmacData), "UTF-8");
	} catch (UnsupportedEncodingException e) {
            System.out.println("Error Generate HMac: e");
	    throw new GeneralSecurityException(e);
	}
    }
        
    public long GetUTCdatetimeAsString(){    
        millis = System.currentTimeMillis();   
        return millis/1000;
    }
    
    public String Decrypt(String data,String utc)throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //System.out.println(data);
        mykey = ApiBPJSEnc.generateKey(Consid+Key+utc);
        data=ApiBPJSEnc.decrypt(data, mykey.getKey(), mykey.getIv());
        data=ApiBPJSLZString.decompressFromEncodedURIComponent(data);
        return data;
    }
    
//    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
//        sslContext = SSLContext.getInstance("SSL");
//        TrustManager[] trustManagers= {
//            new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() {return null;}
//                public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
//                public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
//            }
//        };
//        sslContext.init(null,trustManagers , new SecureRandom());
//        sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//        scheme=new Scheme("https",443,sslFactory);
//        factory=new HttpComponentsClientHttpRequestFactory();
//        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
//        return new RestTemplate(factory);
//    }

    
      public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);

        RestTemplate restTemplate = new RestTemplate(factory);

        ClientHttpRequestInterceptor loggingInterceptor = (request, body, execution) -> {
            
            if (!(request.getMethod() == HttpMethod.POST
                    || request.getMethod() == HttpMethod.PUT
                    || request.getMethod() == HttpMethod.DELETE)) {

                // Selain POST/PUT/DELETE → langsung eksekusi tanpa logging
                return execution.execute(request, body);
            }
            
            long startTime = System.currentTimeMillis();
            ClientHttpResponse response = execution.execute(request, body);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            byte[] responseBody;
            try (InputStream in = response.getBody()) {
                responseBody = (in != null) ? in.readAllBytes() : new byte[0];
            }

            String rawResponse = new String(responseBody, StandardCharsets.UTF_8);
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(rawResponse);
                String metadata = root.path("metaData").toString();

                String decryptedPart = "";
                if (root.has("response")) {
                    String encrypted = root.path("response").asText();
                    if (encrypted != null && !encrypted.isEmpty() && !"null".equalsIgnoreCase(encrypted)) {
                        List<String> tsHeader = request.getHeaders().get("X-Timestamp");
                        String utc = (tsHeader != null && !tsHeader.isEmpty()) ? tsHeader.get(0)
                                : String.valueOf(System.currentTimeMillis() / 1000);
                        decryptedPart = Decrypt(encrypted, utc);
                    }
                }

                lastResponse = metadata + decryptedPart + ";{Response Time: " + duration + " ms}";
                Sequel.menyimpan("trackerjson","now(),?,?,?,?",4,new String[]{ request.getURI().toString(),new String(body, StandardCharsets.UTF_8), lastResponse,"RSPW" + akses.getkode()});
            } catch (Exception e) {
                lastResponse = "Raw Response: " + rawResponse;
                System.out.println(lastResponse);
            }

            return new BufferingClientHttpResponseWrapper(response, responseBody);
        };

        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));
        restTemplate.setInterceptors(new ClientHttpRequestInterceptor[]{loggingInterceptor});

        return restTemplate;
    }

    static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse response;
        private final byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
            this.response = response;
            this.body = body;
        }

        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        public HttpStatus getStatusCode() throws IOException {
            try {
                return response.getStatusCode();
            } catch (Exception e) {
                return null;
            }
        }

        public int getRawStatusCode() throws IOException {
            try {
                return response.getStatusCode().value();
            } catch (Exception e) {
                return 0;
            }
        }

        public String getStatusText() throws IOException {
            try {
                return response.getStatusText();
            } catch (Exception e) {
                return "";
            }
        }

        public void close() {
            try {
                response.close();
            } catch (Exception ignored) {
            }
        }

        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }
    }
}
