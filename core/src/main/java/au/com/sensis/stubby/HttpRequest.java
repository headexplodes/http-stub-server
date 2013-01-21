package au.com.sensis.stubby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HttpRequest extends HttpMessage {

    private static final long serialVersionUID = 1L; // don't care
    
    private String method; // always upper-case
    private String path;
    private Map<String, HttpParam> params = new HashMap<String, HttpParam>();

    private void addParam(String name, String value) {
        HttpParam param = params.get(name);
        if (param == null) {
            param = new HttpParam();
            param.setName(name);
            params.put(name, param);
        }
        param.getValues().add(value);
    }

    @JsonProperty
    public void setQueryParams(List<FlatParam> flattened) {
        for (FlatParam param : flattened) {
            addParam(param.name, param.value);
        }
    }

    @JsonProperty
    public List<FlatParam> getQueryParams() {
        List<FlatParam> result = new ArrayList<FlatParam>();
        for (HttpParam param : params.values()) {
            result.addAll(param.flatten());
        }
        return result;
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

    @JsonIgnore
    public Map<String, HttpParam> getParams() {
        return params;
    }

    @JsonIgnore
    public void setParams(Map<String, HttpParam> params) {
        this.params = params;
    }

}
