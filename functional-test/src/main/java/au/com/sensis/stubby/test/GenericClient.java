package au.com.sensis.stubby.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class GenericClient {

    private URI baseUri;
    private HttpClient httpClient;

    public GenericClient(String baseUri) {
        try {
            this.baseUri = new URI(baseUri);
            this.httpClient = new DefaultHttpClient();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI", e);
        }
    }

    public void close() {
        HttpClientUtils.closeQuietly(httpClient);
    }

    public String makeUri(String path) {
        return baseUri.resolve(path).toString();
    }

    public GenericClientResponse executePost(String path, String body, ContentType contentType) {
        HttpPost request = new HttpPost(makeUri(path));
        request.setEntity(new StringEntity(body, contentType));
        return execute(request);
    }
    
    public GenericClientResponse executeDelete(String path) {
        HttpDelete request = new HttpDelete(makeUri(path));
        return execute(request);
    }
    
    public GenericClientResponse executeGet(String path) {
        HttpGet request = new HttpGet(makeUri(path));
        return execute(request);
    }
    
    public GenericClientResponse execute(HttpUriRequest request) {
        try {
            return new GenericClientResponse(httpClient.execute(request));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error executing '%s' to '%s'", request.getMethod(), request.getURI()), e);
        }
    }
    
}