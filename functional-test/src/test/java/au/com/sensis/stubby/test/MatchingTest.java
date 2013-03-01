package au.com.sensis.stubby.test;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class MatchingTest extends TestBase {
    
    @Test
    public void emptyServer() {
        assertNotFound(client.executeGet("/foo/bar"));
    }
    
    @Test
    public void matchPattern() {
        builder().path("/foo/.*").status(200).stub();
        assertOk(client.executeGet("/foo/bar"));
    }
    
    @Test
    public void notMatchPattern() {
        builder().path("/foo/.*").status(200).stub();
        assertNotFound(client.executeGet("/wrong/bar"));
    }

    @Test
    public void matchQueryParameter() {
        builder().path("/.*").status(200).addQuery("foo", "b..").stub();  // ensure regular expressions supported
        assertOk(client.executeGet("/test?foo=bar&FOO=bar")); // ensure ignores extra parameters, and case sensitive
    }
    @Test
    public void matchMissingParameter() {
        builder().path("/.*").status(200).addQuery("foo", "b..").stub();
        assertNotFound(client.executeGet("/test?blah=wrong"));
    }
    
    @Test
    public void matchHeader() {
        builder().path("/.*").status(200).addRequestHeader("X-Foo", "b..").stub(); // ensure regular expressions supported
        
        HttpGet request = new HttpGet(makeUri("/test"));
        request.setHeader("x-foo", "bar"); // assert case-insensitive
        request.setHeader("ignore", "blah");
        
        assertOk(client.execute(request));
    }
    
    @Test
    public void matchMissingHeader() {
        builder().path("/.*").status(200).addRequestHeader("X-Foo", "b..").stub();
        
        HttpGet request = new HttpGet(makeUri("/test"));
        request.setHeader("ignore", "blah");
        
        assertNotFound(client.execute(request));
    }
    
    @Test
    public void matchMethod() {
        builder().path("/.*").method("P.+").status(200).stub();
                
        assertOk(client.execute(new HttpPut(makeUri("/test"))));  // ensure regular expressions supported
        assertOk(client.execute(new HttpPost(makeUri("/test"))));
    }
    
}