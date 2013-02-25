package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.com.sensis.stubby.test.model.JsonExchangeList;

/*
 * When two identical request patterns are stubbed, the previous one should be deleted
 * and the new one inserted at the start of the list.
 */
public class DuplicateRequestPatternTest extends TestBase {

    private void assertSame(MessageBuilder... messages) {
        int status = 1; // use status code to identify requests
        for (MessageBuilder message : messages) {
            message.status(status++).stub();

            JsonExchangeList responses = responses();
            assertEquals(1, responses.size());
            assertEquals(status, (int)responses.get(0).response.status);

            status++;
        }
    }
    
    @SuppressWarnings("unused") // soon, soon...
    private void assertNotSame(MessageBuilder... messages) {
        int status = 1; // use status code to identify requests
        for (MessageBuilder message : messages) {
            message.status(status++).stub();

            JsonExchangeList responses = responses();
            assertEquals(status, responses.size()); // size should grow
            assertEquals(status, (int)responses.get(0).response.status);

            status++;
        }
    }

    @Test
    public void pathSame() {
        assertSame(
                builder().path("/foo"),
                builder().path("/foo"));
    }
    
    @Test
    public void pathDiffers() {
        assertSame(
                builder().path("/foo1"),
                builder().path("/foo2"));
    }
    
    @Test
    public void querySame() {
        assertSame(
                builder().path("/foo").query("foo", "bar"),
                builder().path("/foo").query("foo", "bar"));
    }    
    
    @Test
    public void queryDiffers() {
        assertSame(
                builder().path("/foo").query("foo", "bar1"),
                builder().path("/foo").query("foo", "bar2"));
    }

}