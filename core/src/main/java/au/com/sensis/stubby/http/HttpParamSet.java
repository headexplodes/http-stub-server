package au.com.sensis.stubby.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.sensis.stubby.FlatParam;
import au.com.sensis.stubby.utils.CaseInsensitiveMap;

public class HttpParamSet { // set of key-value pairs

    private boolean caseInsensitive;
    private Map<String, HttpParam> params;

    public HttpParamSet(boolean caseInsensitive) { // eg, header names are case-insensitive
        this.caseInsensitive = caseInsensitive;
        if (caseInsensitive) {
            this.params = new CaseInsensitiveMap<HttpParam>();
        } else {
            this.params = new TreeMap<String, HttpParam>();
        }
    }
    
    public HttpParamSet(HttpParamSet other) { // copy constructor
        this(other.caseInsensitive);
        for (Map.Entry<String, HttpParam> entry : other.params.entrySet()) {
            this.params.put(entry.getKey(), new HttpParam(entry.getValue()));
        }
    }

    public void add(String name, String value) {
        HttpParam param = params.get(name);
        if (param == null) {
            param = new HttpParam(name);
            params.put(name, param);
        }
        param.getValues().add(value);
    }
    
    public void add(String name, List<String> values) {
        HttpParam param = params.get(name);
        if (param == null) {
            param = new HttpParam(name);
            params.put(name, param);
        }
        param.getValues().addAll(values);
    }

    public void addAll(List<FlatParam> flattened) {
        for (FlatParam param : flattened) {
            add(param.getName(), param.getValue());
        }
    }

    public void set(String name, String value) { // removes any existing value first
        params.remove(name);
        add(name, value);
    }

    public List<FlatParam> flatten() {
        List<FlatParam> result = new ArrayList<FlatParam>();
        for (HttpParam header : params.values()) {
            result.addAll(header.flatten());
        }
        return result;
    }

    public Map<String, HttpParam> getMap() {
        return params;
    }

    public List<String> getValues(String key) {
        HttpParam param = params.get(key);
        if (param != null) {
            return param.getValues();
        } else {
            return null; // not found
        }
    }

    public String getValue(String key) { // assume only single value
        HttpParam param = params.get(key);
        if (param != null) {
            return param.value();
        } else {
            return null; // not found
        }
    }

}
