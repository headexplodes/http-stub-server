package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/*
 * When two identical request patterns are stubbed, the previous one should be deleted
 * and the new one inserted at the start of the list.
 */
public class DuplicateRequestPatternTest extends TestBase {

    private void assertSame(MessageBuilder... messages) {
        int status = 1; // use status code to identify requests
        for (MessageBuilder message : messages) {
            message.status(status++).stub();

            List<MessageBuilder> responses = responses();
            assertEquals(1, responses.size());
            assertEquals(status, responses.get(0).response.statusCode);

            status++;
        }
    }
    
    @SuppressWarnings("unused") // soon, soon...
    private void assertNotSame(MessageBuilder... messages) {
        int status = 1; // use status code to identify requests
        for (MessageBuilder message : messages) {
            message.status(status++).stub();

            List<MessageBuilder> responses = responses();
            assertEquals(status, responses.size()); // size should grow
            assertEquals(status, responses.get(0).response.statusCode);

            status++;
        }
    }

    @Test
    public void minimal() {
        assertSame(
                builder().path("/foo"),
                builder().path("/foo"));
    }

}