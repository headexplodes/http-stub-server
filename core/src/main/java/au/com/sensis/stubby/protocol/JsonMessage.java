package au.com.sensis.stubby.protocol;

import java.util.List;

public class JsonMessage {

    private List<JsonParam> headers;
    private Object body;

    public List<JsonParam> getHeaders() {
        return headers;
    }

    public void setHeaders(List<JsonParam> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

}
