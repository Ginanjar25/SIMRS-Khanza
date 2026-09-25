/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modif;

/**
 *
 * @author shodi
 */
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.http.client.ClientHttpResponseWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Interceptor untuk logging HTTP Request dan Response
 * Tidak mengubah fungsi asli dari API call
 */
public class BPJSApiInterceptor implements ClientHttpRequestInterceptor {
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, 
                                       ClientHttpRequestExecution execution) throws IOException {
        
        // Log Request
        logRequest(request, body);
        
        // Execute request
        ClientHttpResponse response = execution.execute(request, body);
        
        // Log Response
        logResponse(response);
        
        return response;
    }
    
    private void logRequest(HttpRequest request, byte[] body) {
        System.out.println("\n========== BPJS API REQUEST ==========");
        System.out.println("Timestamp: " + dateFormat.format(new Date()));
        System.out.println("URI: " + request.getURI());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Headers:");
        request.getHeaders().forEach((name, values) -> 
            values.forEach(value -> System.out.println("  " + name + ": " + value))
        );
        
        if (body.length > 0) {
            System.out.println("Request Body:");
            System.out.println(new String(body, StandardCharsets.UTF_8));
        }
        System.out.println("======================================\n");
    }
    
    private void logResponse(ClientHttpResponse response) throws IOException {
        System.out.println("\n========== BPJS API RESPONSE ==========");
        System.out.println("Timestamp: " + dateFormat.format(new Date()));
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Status Text: " + response.getStatusText());
        System.out.println("Headers:");
        response.getHeaders().forEach((name, values) -> 
            values.forEach(value -> System.out.println("  " + name + ": " + value))
        );
        
        // Read response body
        byte[] bodyBytes = StreamUtils.copyToByteArray(response.getBody());
        if (bodyBytes.length > 0) {
            String responseBody = new String(bodyBytes, StandardCharsets.UTF_8);
            System.out.println("Response Body:");
            System.out.println(responseBody);
        }
        System.out.println("========================================\n");
    }
}
