package au.com.sensis.stubby.protocol;

import java.util.List;

public class JsonRequest extends JsonMessage {

    private String method;
    private String path;
    private List<JsonParam> params;

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

    public List<JsonParam> getParams() {
        return params;
    }

    public void setParams(List<JsonParam> params) {
        this.params = params;
    }

}
