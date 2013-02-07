package au.com.sensis.stubby.service.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import au.com.sensis.stubby.model.StubMessage;

public class EmptyBodyPatternTest {

    @Test
    public void testEquality() {
        assertEquals(new EmptyBodyPattern(), new EmptyBodyPattern());
    }

    @Test
    public void testMatchesAnything() {
        assertTrue(new EmptyBodyPattern().matches(new StubMessage() {} ));
    } 
    
}
