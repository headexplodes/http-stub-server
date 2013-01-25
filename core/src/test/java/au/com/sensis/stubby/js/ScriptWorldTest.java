package au.com.sensis.stubby.js;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.model.StubResponse;

public class ScriptWorldTest {

    private StubExchange exchange;
    
    @Before
    public void before() {
        exchange = new StubExchange();
        exchange.setRequest(new StubRequest());
        exchange.setResponse(new StubResponse());
        exchange.setDelay(123L);
    }
    
    @Test
    public void testCopiesFields() {
        ScriptWorld world = new ScriptWorld(exchange);
        
        assertTrue(world.getRequest() != exchange.getRequest()); // should be different instances
        assertTrue(world.getResponse() != exchange.getResponse());

        assertTrue(world.getDelay().equals(exchange.getDelay())); // immutable anyway...
    }
    
}
