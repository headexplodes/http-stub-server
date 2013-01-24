package au.com.sensis.stubby.model;

import java.util.ArrayList;
import java.util.List;

public class StubRequest extends StubMessage {

    private String method;
    private String path;
    private List<StubParam> params;

    public StubRequest() { }
    
    public StubRequest(StubRequest other) { // copy constructor
        super(other);
        this.method = other.method;
        this.path = other.path;
        this.params = new ArrayList<StubParam>();
        for (StubParam param : other.params) {
            params.add(new StubParam(param));
        }
    }
    
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<StubParam> getParams() {
        return params;
    }

    public void setParams(List<StubParam> params) {
        this.params = params;
    }

}
