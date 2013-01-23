package au.com.sensis.stubby;

public class FlatParam { // used for JSON de/serialisation

    private String name;
    private String value;

    public FlatParam() { }

    public FlatParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public FlatParam(FlatParam other) { // copy constructor
        this.name = other.name;
        this.value = other.value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
