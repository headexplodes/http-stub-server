package au.com.sensis.stubby.test;

import org.junit.Test;

public class FooTest extends TestBase {
    
    @Test
    public void fooTest() {
        builder().path("/foo/bar").status(200).body("Sup?").stub();
        assertOk(client.executeGet("/foo/bar"));
    }

}