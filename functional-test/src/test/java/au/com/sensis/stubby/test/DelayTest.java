package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class DelayTest extends TestBase {
    
    @Test
    public void delayTest() {
        long millis = 2000;
        long tollerance = 500;
        
        builder().path("/foo").status(200).delay(millis).stub();
        
        long started = System.currentTimeMillis();
        assertOk(client.executeGet("/foo"));
        long ended = System.currentTimeMillis();
        
        assertEquals(millis, Math.abs(ended-started), tollerance); // assert delay within tollerance
    }

}