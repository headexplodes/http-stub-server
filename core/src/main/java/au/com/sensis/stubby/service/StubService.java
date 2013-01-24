package au.com.sensis.stubby.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.http.HttpResponse;
import au.com.sensis.stubby.js.ScriptWorld;
import au.com.sensis.stubby.service.model.StubExchange;
import au.com.sensis.stubby.utils.JsonUtils;
import au.com.sensis.stubby.utils.Pair;

public class StubService {

    private static final Logger LOGGER = Logger.getLogger(StubService.class);

    private LinkedList<StubExchange> responses = new LinkedList<StubExchange>();
    private LinkedList<HttpRequest> requests = new LinkedList<HttpRequest>();

    public synchronized void addResponse(StubExchange exchange) {
        LOGGER.debug("Adding response: " + JsonUtils.prettyPrint(exchange));
        responses.remove(exchange); // remove existing stubed request (ie, will never match anymore)
        responses.addFirst(exchange); // ensure most recent match first   
    }

    public synchronized Pair<HttpResponse,Long> findMatch(HttpRequest request) { // Pair<response, delay>
        try {
            LOGGER.trace("Got request: " + JsonUtils.prettyPrint(request));
            requests.addFirst(request);
            for (StubExchange stubbedResponse : responses) {
                if (stubbedResponse.matches(request)) {
                    LOGGER.info("Matched: " + request.getPath() + "");

                    if (stubbedResponse.getScript() != null) {
                        ScriptWorld world = ScriptWorld.create(request, stubbedResponse); // creates deep copies of objects
                        stubbedResponse.getScript().execute(world);
                        return new Pair<HttpResponse,Long>(
                                world.getResponse(),
                                world.getDelay());
                    } else {
                        return new Pair<HttpResponse,Long>(
                                stubbedResponse.getResponse(),
                                stubbedResponse.getDelay());
                    }
                }
            }
            LOGGER.info("Didn't match: " + request.getPath());
            return null; // no match
        } catch (Exception e) {
            throw new RuntimeException("Error matching request", e);
        }
    }

    public synchronized StubExchange getResponse(int index) throws NotFoundException {
        try {
            return responses.get(index);
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Response does not exist: " + index);
        }
    }

    public synchronized List<StubExchange> getResponses() {
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
