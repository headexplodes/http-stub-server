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

    private static final String P_TEST_SERVER = "test.server"; // server to test against
    
    private Client client;

    protected String getStandaloneServer() {
        if (!TestServer.isRunning()) { // keep running for all tests
            TestServer.start();
        }
        return String.format("http://localhost:%d", TestServer.getPort());
    }
    
    @Before
    public void before() {
        String testServer = System.getProperty(P_TEST_SERVER);
        
        if (testServer == null) { // property not given, start internal server
            testServer = getStandaloneServer();
        }

        client = new Client(testServer);
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
