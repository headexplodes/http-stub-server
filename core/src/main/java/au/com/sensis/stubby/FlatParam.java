package au.com.sensis.stubby;

public class FlatParam { // used for JSON de/serialisation

    public String name;
    public String value;

    public FlatParam() {}

    public FlatParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
