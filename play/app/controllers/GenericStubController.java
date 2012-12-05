package controllers;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Controller;
import au.com.sensis.stubby.HttpMessage;
import au.com.sensis.stubby.StubbedResponse;
import au.com.sensis.stubby.play.Transformer;
import au.com.sensis.stubby.utils.UriUtils;

/*
 * Generic stubbed controller that can be used to stub any URL
 * Stubbed URLs can contain wildcards, eg: /path/*?param=*
 */
public class GenericStubController extends Controller {

    private static final Logger LOGGER = Logger.getLogger(GenericStubController.class);
       
    public static LinkedList<StubbedResponse> RESPONSES = new LinkedList<StubbedResponse>();
    public static LinkedList<HttpMessage> REQUESTS = new LinkedList<HttpMessage>();
    
    private static ObjectMapper JSON = new ObjectMapper();
    
    public static synchronized void match() throws Exception {
        HttpMessage lastRequest = Transformer.fromPlayRequest(request);
        REQUESTS.addFirst(lastRequest);
        LOGGER.trace("Got request: " + JSON.writerWithDefaultPrettyPrinter().writeValueAsString(lastRequest));
        for (StubbedResponse stubbedResponse : RESPONSES) {
            if (stubbedResponse.matches(lastRequest)) {
                LOGGER.info("Matched '" + UriUtils.stripHost(request.url) + "'");
                Transformer.populatePlayResponse(stubbedResponse.response, response);
                return; // match
            } else {
                LOGGER.info("Didn't match '" + UriUtils.stripHost(request.url) + "'");
            }
        }
        notFound("No stubbed method matched request"); // no match found
    }

    public static synchronized void stub() throws Exception {
        if (!"application/json".equals(request.contentType)) {
            error(415, "Unsupported Media Type");
        }
        StubbedResponse stubbedResponse = JSON.readValue(request.body, StubbedResponse.class);
        LOGGER.info("Stubbing: " + JSON.writerWithDefaultPrettyPrinter().writeValueAsString(stubbedResponse));
        RESPONSES.addFirst(stubbedResponse); // ensure most recent match first
        ok();
    }

    public static synchronized void last(int index) throws Exception {
        try {
            renderJSON(JSON.writeValueAsString(REQUESTS.get(index)));
        } catch (IndexOutOfBoundsException e) {
            notFound("No last request for index " + index);
        }   
    }
    
    public static synchronized void reset() {
        REQUESTS.clear();
        RESPONSES.clear();
        ok();
    }

}
