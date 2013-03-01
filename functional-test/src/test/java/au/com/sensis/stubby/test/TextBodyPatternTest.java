package au.com.sensis.stubby.test;

import org.apache.http.entity.ContentType;
import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class TextBodyPatternTest extends TestBase {
    
    @Test
    public void wildcardPattern() {        
        builder().path("/foo").status(200).requestBody(".*").stub();
        
        assertOk(client.executePost("/foo", "test", ContentType.TEXT_PLAIN));
    }
    
    @Test
    public void withPatternMatch() {        
        builder().path("/foo").status(200).requestBody("foo b.+").stub();
        
        assertOk(client.executePost("/foo", "foo bar", ContentType.TEXT_PLAIN));
    }
    
    @Test
    public void withPatternNotMatch() {        
        builder().path("/foo").status(200).requestBody("foo b.+").stub();
        
        assertNotFound(client.executePost("/foo", "incorrect", ContentType.TEXT_PLAIN));
    }
        
    @Test
    public void notText() {
        builder().path("/foo").status(200).requestBody(".*").stub();
        
        assertNotFound(client.executePost("/foo", "test", ContentType.APPLICATION_JSON));
    }

}