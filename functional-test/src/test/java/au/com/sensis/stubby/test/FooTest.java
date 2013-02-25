package au.com.sensis.stubby.test;

import org.junit.Test;

public class FooTest extends TestBase {
    
    @Test
    public void fooTest() {
        postFile("fooBar.json");
        assertOk(client.executeGet("/foo/bar"));
    }

}