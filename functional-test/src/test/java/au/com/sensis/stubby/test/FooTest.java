package au.com.sensis.stubby.test;

import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

public class FooTest extends TestBase {
    
    @Test
    public void fooTest() {
        postFile("fooBar.json");
        assertOk(new HttpGet(makeUri("/foo/bar")));
    }

}