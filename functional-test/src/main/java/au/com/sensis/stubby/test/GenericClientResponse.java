package au.com.sensis.stubby.test;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.util.EntityUtils;

import au.com.sensis.stubby.utils.JsonUtils;

public class GenericClientResponse {

    private HttpResponse response;

    public GenericClientResponse(HttpResponse response) {
        this.response = response;
    }

    public void close() {
        if (hasBody()) {
            EntityUtils.consumeQuietly(response.getEntity()); // ensure we've read everything
        }
        HttpClientUtils.closeQuietly(response);
    }

    public boolean hasBody() {
        try {
            return response.getEntity() != null && response.getEntity().getContent() != null;
        } catch (IOException e) {
            throw new RuntimeException("Error getting entity", e);
        }
    }

    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }

    public GenericClientResponse assertOk() {
        if (!isOk()) {
            throw new RuntimeException("Server returned " + getStatus());
        }
        return this; // helpful for chaining method calls
    }

    public boolean isOk() {
        return getStatus() == HttpStatus.SC_OK;
    }

    public <T> T getJson(Class<T> type) {
        if (hasBody()) {
            try {
                return JsonUtils.deserialize(response.getEntity().getContent(), type);
            } catch (IOException e) {
                throw new RuntimeException("Error getting entity", e);
            } finally {
                close();
            }
        } else {
            throw new IllegalStateException("Response does not have a body");
        }
    }

    public String getText() throws IOException {
        if (hasBody()) {
            try {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            } finally {
                close();
            }
        } else {
            throw new IllegalStateException("Response does not have a body");
        }
    }

}
