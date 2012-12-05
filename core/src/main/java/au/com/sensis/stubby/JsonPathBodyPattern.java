package au.com.sensis.stubby;

public class JsonPathBodyPattern { /*extends BodyPattern {

    private List<JsonPath> tests = new ArrayList<JsonPath>();

    @Override
    public boolean matches(HttpMessage request) {
        if (request.getBody() == null) {
            return false; // expected a body
        }
        for (JsonPath test : tests) {
            boolean matched = test.read(request.getBody().toString());
            if (!matched) { // all patterns must match
                return false; 
            }
        }
        return true;
    }
    
    @Override
    public String expectedValue() {
        List<String> result = new ArrayList<String>();
        for (JsonPath pattern : tests) {
            result.add("'" + pattern.getPath() + "'");
        }
        return result.toString();
    }

    public void addTest(JsonPath test) {
        tests.add(test);
    }
    
    public void addTest(String test) {
        tests.add(JsonPath.compile(test));
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof JsonBodyPattern)
                && ((JsonPathBodyPattern)obj).tests.equals(tests);
    }
    
    @Override
    public int hashCode() {
        return tests.hashCode();
    }
*/
}
