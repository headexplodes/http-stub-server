package au.com.sensis.stubby.test;

import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

public class ScriptTest extends TestBase {
    
    @Test
    public void scriptTest() {
        postFile("scriptBar.json");
        assertOk(client.executeGet("/script/bar?run=true"));
        assertStatus(500, execute(new HttpGet(makeUri("/script/bar?run=false"))));
    }

}