package au.com.sensis.stubby.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public abstract class TestBase {

    private Client client;

    @Before
    public void before() {
        client = new Client("http://localhost:8080"); // TODO: make configurable...
        client.reset();
    }

    @After
    public void after() {
        client.close();
    }

    protected void postFile(String filename) {
        String path = "/tests/" + filename;
        InputStream resource = getClass().getResourceAsStream(path);
        if (resource != null) {
            client.postMessage(resource);
        } else {
            throw new RuntimeException("Resource not found: " + path);
        }
    }

    protected String makeUri(String path) {
        return client.makeUri(path);
    }

    protected void close(HttpResponse response) {
        HttpClientUtils.closeQuietly(response);
    }

    protected HttpResponse execute(HttpUriRequest request) {
        try {
            return client.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Client client() {
        return client;
    }
    
    protected List<MessageBuilder> responses() {
        return client.getResponses();
    }

    protected void assertOk(HttpUriRequest request) {
        HttpResponse response = execute(request);
        try {
            assertOk(response);
        } finally {
            close(response);
        }
    }

    protected void assertOk(HttpResponse response) {
        assertStatus(HttpStatus.SC_OK, response);
    }

    protected void assertStatus(int status, HttpResponse response) {
        Assert.assertEquals(status, response.getStatusLine().getStatusCode());
    }
    
    protected MessageBuilder builder() {
        return new MessageBuilder(client);
    }

}
