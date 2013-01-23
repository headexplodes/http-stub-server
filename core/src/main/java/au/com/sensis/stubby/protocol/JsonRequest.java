package au.com.sensis.stubby.protocol;

import java.util.List;

public class JsonRequest extends JsonMessage {

    private String method;
    private String path;
    private List<JsonParam> queryParams;

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

    public List<JsonParam> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<JsonParam> queryParams) {
        this.queryParams = queryParams;
    }

}
