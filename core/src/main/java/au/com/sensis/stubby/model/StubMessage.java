package au.com.sensis.stubby.model;

import java.util.ArrayList;
import java.util.List;

import au.com.sensis.stubby.utils.DeepCopyUtils;

public abstract class StubMessage {

    private List<StubParam> headers;
    private Object body;

    protected StubMessage() { }
    
    protected StubMessage(StubMessage other) { // copy constructor
        this.body = DeepCopyUtils.deepCopy(other.body);
        this.headers = new ArrayList<StubParam>();
        for (StubParam param : other.headers) {
            headers.add(new StubParam(param));
        }
    }
    
    public List<StubParam> getHeaders() {
        return headers;
    }
    
    public StubParam getHeader(String name) { // get first, case insensitive lookup
        for (StubParam header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null; // not found
    }

    public void setHeaders(List<StubParam> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

}
