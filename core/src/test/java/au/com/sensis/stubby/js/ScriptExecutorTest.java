package au.com.sensis.stubby.js;

import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.http.HttpResponse;

public class ScriptExecutorTest {

    private ScriptWorld world;
    
    @Before
    public void before() {
        world = new ScriptWorld();
        world.setRequest(new HttpRequest());
        world.setResponse(new HttpResponse());
    }
    
    private void execute(String script) {
        new Script(script).execute(world);
    }
    
    @Test
    public void testEmptyScript() {
        execute(""); // make sure it doesn't fail
    }
    
    // TODO
    
}
