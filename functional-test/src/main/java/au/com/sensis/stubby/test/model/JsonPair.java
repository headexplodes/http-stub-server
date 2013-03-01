package au.com.sensis.stubby.test.model;

public class JsonPair {
    
    public String name;
    public String value;
    
    public JsonPair() { } // for Jackson
    
    public JsonPair(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
