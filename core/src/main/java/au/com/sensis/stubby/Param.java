package au.com.sensis.stubby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Param implements Serializable {

    private static final long serialVersionUID = 1L; // don't care
    
    private String name;
    private List<String> values = new ArrayList<String>();

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
