package au.com.sensis.stubby.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;

public class Client {

    private URI baseUri;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    public Client(String baseUri) {
        try {
            this.baseUri = new URI(baseUri);
            this.httpClient = new DefaultHttpClient();
            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
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

    public void postMessage(InputStream stream) {
        String message;
        try {
            message = IOUtils.toString(stream);
        } catch (IOException e) {
            throw new RuntimeException("Error", e);
        }
        postMessage(message);
    }

    public void postMessage(MessageBuilder message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Error generating JSON", e);
        }
        postMessage(json);
    }

    public void postMessage(String message) {
        try {
            HttpPost post = new HttpPost(makeUri("/_control/responses"));
            post.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));
            HttpResponse response = httpClient.execute(post);
            try {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new IOException("Status code " + response.getStatusLine());
                }
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error posting to stub server", e);
        }
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }

    public List<MessageBuilder> getResponses() {
        try {
            HttpGet request = new HttpGet(makeUri("/_control/responses"));
            HttpResponse response = httpClient.execute(request);
            try {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new IOException("Status code " + response.getStatusLine());
                }
                return objectMapper.readValue(response.getEntity().getContent(), new TypeReference<List<MessageBuilder>>() {});
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting from stub server", e);
        }
    }
    
    public void reset() {
        deleteResponses();
        //deleteRequests();
    }

    public void deleteResponses() {
        try {
            HttpDelete request = new HttpDelete(makeUri("/_control/responses"));
            HttpResponse response = httpClient.execute(request);
            try {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new IOException("Status code " + response.getStatusLine());
                }
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting to stub server", e);
        }
    }
    
}