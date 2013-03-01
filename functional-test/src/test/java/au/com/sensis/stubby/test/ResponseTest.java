package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class ResponseTest extends TestBase {
    
    @Test
    public void testAllFields() {
        builder()
            .path("/.*")
            .status(201)
            .addResponseHeader("X-Foo", "bar1")
            .addResponseHeader("X-Foo", "bar2") // two values for single name
            .addResponseHeader("x-foo", "bar3; bar4") // check case-insensitivity
            .responseBody("response body")
            .stub(); 
        
        GenericClientResponse response = client.executeGet("/test");
        
        assertEquals(201, response.getStatus());
        assertEquals(Arrays.asList("bar1", "bar2", "bar3; bar4"), response.getHeaders("X-Foo"));
        assertEquals("response body", response.getText());
    }

}
