package au.com.sensis.stubby.test;

import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

public class ScriptTest extends TestBase {
       
    @Test
    public void scriptTest() {
        String script = "if (request.getParam('run') == 'true') { response.statusCode = 200; response.body = request.getParam('run'); }";
        
        builder().path("/script/bar").status(500).body("Sup?").script(script).stub();

        assertOk(client.executeGet("/script/bar?run=true"));
        assertStatus(500, execute(new HttpGet(makeUri("/script/bar?run=false"))));
    }

}