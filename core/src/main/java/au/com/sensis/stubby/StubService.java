package au.com.sensis.stubby;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import au.com.sensis.stubby.utils.Pair;

public class StubService {

    private static final Logger LOGGER = Logger.getLogger(StubService.class);

    private LinkedList<StubbedResponse> responses = new LinkedList<StubbedResponse>();
    private LinkedList<HttpRequest> requests = new LinkedList<HttpRequest>();

    private ObjectMapper mapper() {
        return new ObjectMapper();
    }
    
    private String prettyPrint(Object value) {
        try {
            return mapper().writerWithDefaultPrettyPrinter().writeValueAsString(value); // TODO: add utility method for this...
        } catch (IOException e) {
            throw new RuntimeException("Error rendering JSON", e);
        }
    }

    public synchronized void addResponse(StubbedResponse response) {
        LOGGER.info("Adding response: " + prettyPrint(response));
        responses.remove(response); // remove existing stubed request (ie, will never match anymore)
        responses.addFirst(response); // ensure most recent match first   
    }

    public synchronized void addResponse(String responseJson) {
        try {
            addResponse(mapper().readValue(responseJson, StubbedResponse.class));
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    public synchronized Pair<HttpResponse,Long> findMatch(HttpRequest request) { // Pair<response, delay>
        try {
            LOGGER.trace("Got request: " + prettyPrint(request));
            requests.addFirst(request);
            for (StubbedResponse stubbedResponse : responses) {
                if (stubbedResponse.matches(request)) {
                    LOGGER.info("Matched: " + request.getPath() + "");
                    return new Pair<HttpResponse,Long>(
                            stubbedResponse.getResponse(), 
                            stubbedResponse.getDelay());
                }
            }
            LOGGER.info("Didn't match: " + request.getPath());
            return null; // no match
        } catch (Exception e) {
            throw new RuntimeException("Error matching request", e);
        }
    }

    public synchronized StubbedResponse getResponse(int index) throws NotFoundException {
        try {
            return responses.get(index);
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Response does not exist: " + index);
        }
    }

    public synchronized List<StubbedResponse> getResponses() {
        return responses;
    }

    public synchronized void deleteResponse(int index) throws NotFoundException {
        LOGGER.trace("Deleting response: " + index);
        try {
            responses.remove(index);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Response does not exist: " + index);
        }
    }

    public synchronized void deleteResponses() {
        LOGGER.trace("Deleting all responses");
        responses.clear();
    }

    public synchronized HttpRequest getRequest(int index) throws NotFoundException {
        try {
            return requests.get(index);
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Response does not exist: " + index);
        }
    }

    public synchronized List<HttpRequest> getRequests() {
        return requests;
    }

    public synchronized void deleteRequest(int index) throws NotFoundException {
        LOGGER.trace("Deleting request: " + index);
        try {
            requests.remove(index);
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Request does not exist: " + index);
        }
    }

    public synchronized void deleteRequests() {
        LOGGER.trace("Deleting all requests");
        requests.clear();
    }

}
