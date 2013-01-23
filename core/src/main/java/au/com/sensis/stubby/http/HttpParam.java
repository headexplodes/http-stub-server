package au.com.sensis.stubby.http;

import java.util.ArrayList;
import java.util.List;

import au.com.sensis.stubby.FlatParam;

public class HttpParam { // parameter with multiple values (eg, header, query parameter etc.)

    private String name;
    private List<String> values;

    public HttpParam(String name) {
        this.name = name;
        this.values = new ArrayList<String>();
    }
    
    public HttpParam(HttpParam param) { // copy constructor
        this.name = param.name;
        this.values = new ArrayList<String>(param.values);
    }
    
    public String value() {
        if (values.size() == 1) {
            return values.get(0);
        } else {
            throw new RuntimeException("Expected only single value: " + name);
        }
    }

    public List<FlatParam> flatten() {
        List<FlatParam> result = new ArrayList<FlatParam>();
        for (String value : values) {
            result.add(new FlatParam(name, value));
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String toString() {
        return values.toString();
    }

}
