package au.com.sensis.stubby;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.stubby.http.HttpMessage;
import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.utils.JsonUtils;

public class JsonBodyPatternTest {
    
    private ObjectMapper mapper = JsonUtils.defaultMapper();
    
    private Object parse(String json) {
        try {
            return mapper.readValue(json, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    private HttpRequest message(String bodyJson) {
        HttpRequest message = new HttpRequest();
        message.setBody(parse(bodyJson));
        message.setContentType("application/json");
        return message;
    }
    
    private JsonBodyPattern pattern(String patternJson) {
        return new JsonBodyPattern(parse(patternJson));
    }
    
    private class PartialAssert {
        private JsonBodyPattern pattern;
        
        public PartialAssert(String pattern) {
            this.pattern = pattern(pattern);
        }
        
        public void matches(String request) {
            JsonBodyPattern.MatchResult result = pattern.matchResult(message(request));
            Assert.assertTrue(result.getReason(), result.isMatch());
        }
        
        public void doesNotMatch(String request) {
            JsonBodyPattern.MatchResult result = pattern.matchResult(message(request));
            Assert.assertFalse(result.getReason(), result.isMatch());
        }   
    }
    
    private PartialAssert assertPattern(String pattern) {
        return new PartialAssert(pattern);
    }
    
    @Test
    public void testInvalidContentType() throws Exception {
        HttpMessage request = message("{}");
        request.setContentType("text/plain");
        Assert.assertFalse(pattern("{}").matches(request));
    }
    
    @Test
    public void testEmptyPattern() throws Exception {
        assertPattern("{}").matches("{}");
        assertPattern("{}").matches("{\"any\":\"value\"}");
        assertPattern("[]").matches("[]");
        assertPattern("[]").matches("[{\"any\":\"value\"}]");
    }
    
    @Test
    public void testArrayNotMatchObject() throws Exception {
        assertPattern("[]").doesNotMatch("{}");
    }
    
    @Test
    public void testObjectNotMatchArray() throws Exception {
        assertPattern("{}").doesNotMatch("[]");
    }
    
    @Test
    public void simpleObjectMatch() throws Exception {
        assertPattern("{\"foo\":\"bar\"}").matches("{\"foo\":\"bar\"}");
        assertPattern("{\"foo\":\"bar\"}").doesNotMatch("{\"foo\":\"blah\"}");
        assertPattern("{\"foo\":\"bar\"}").doesNotMatch("{\"foo2\":\"bar\"}");
        assertPattern("{\"foo\":\"bar\"}").doesNotMatch("{}");
    }
    
    @Test
    public void testRegexMatch() throws Exception {
        assertPattern("{\"foo\":\".*\"}").matches("{\"foo\":\"bar\"}");
        assertPattern("{\"foo\":\".*\"}").matches("{\"foo\":\"\"}");
        assertPattern("{\"foo\":\".*\"}").doesNotMatch("{}");
        assertPattern("{\"foo\":\"(true|false)\"}").matches("{\"foo\":true}");
        assertPattern("{\"foo\":\"(true|false)\"}").matches("{\"foo\":false}");
        assertPattern("{\"foo\":\"(true|false)\"}").matches("{\"foo\":\"false\"}");
        assertPattern("{\"foo\":\"(true|false)\"}").doesNotMatch("{\"foo\":1}");
        assertPattern("{\"foo\":\"[12]3\"}").matches("{\"foo\":13}");
        assertPattern("{\"foo\":\"[12]3\"}").matches("{\"foo\":23}");
        assertPattern("{\"foo\":\"[12]3\"}").matches("{\"foo\":\"23\"}");
        assertPattern("{\"foo\":\"[12]3\"}").doesNotMatch("{\"foo\":33}");
    }
    
    @Test
    public void testNumberMatch() throws Exception {
        assertPattern("{\"foo\":123}").matches("{\"foo\":123}");
        assertPattern("{\"foo\":1.23}").matches("{\"foo\":1.23}");
        assertPattern("{\"foo\":1.234}").doesNotMatch("{\"foo\":1.23}");
        assertPattern("{\"foo\":123}").doesNotMatch("{\"foo\":\"123\"}");
    }
    
    @Test
    public void testBooleanMatch() throws Exception {
        assertPattern("{\"foo\":true}").matches("{\"foo\":true}");
        assertPattern("{\"foo\":false}").matches("{\"foo\":false}");
        assertPattern("{\"foo\":false}").doesNotMatch("{\"foo\":\"false\"}");
    }
    
    @Test
    public void testNullValues() throws Exception {
        assertPattern("{\"foo\":null}").matches("{\"foo\":null}");
        assertPattern("{\"foo\":null}").matches("{}");
        assertPattern("{\"foo\":null}").doesNotMatch("{\"foo\":\"null\"}");
    }
    
    @Test
    public void testArrayMatching() throws Exception {
        assertPattern("[]").matches("[1,2]");
        assertPattern("[1,2]").matches("[1,2]");
        assertPattern("[2,4]").matches("[1,2,3,4]");
        assertPattern("[3,2]").doesNotMatch("[1,2,3,4]"); // pattern elements must be found in order
        assertPattern("[{\"foo\":true}]").matches("[{\"foo\":true}]");
        assertPattern("[{\"first\":true},{\"second\":true}]").matches("[{\"first\":true},{\"second\":true}]");
        assertPattern("[{\"first\":true},{\"second\":true}]").doesNotMatch("[{\"second\":true},{\"first\":true}]");
    }
    
    @Test
    public void testNesting() throws Exception {
        assertPattern("{\"foo\":{\"bar\":true}}").matches("{\"foo\":{\"bar\":true}}");
        assertPattern("{\"foo\":{\"bar\":true}}").doesNotMatch("{\"foo\":{\"bar\":false}}");
        assertPattern("{\"foo\":{\"bar\":[]}}").matches("{\"foo\":{\"bar\":[]}}");
        assertPattern("{\"foo\":{\"bar\":[]}}").matches("{\"foo\":{\"bar\":[{}]}}");
        assertPattern("{\"foo\":{\"bar\":[]}}").doesNotMatch("{\"foo\":{\"bar\":{}}}");
    }
    
}
