package au.com.sensis.stubby;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import au.com.sensis.stubby.MatchResult.Field;
import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.http.HttpResponse;
import au.com.sensis.stubby.js.Script;

public class StubExchange {

    private static final Logger LOGGER = Logger.getLogger(StubExchange.class);

    private RequestPattern request = new RequestPattern();
    private HttpResponse response = new HttpResponse();
    private Long delay; // milliseconds
    private Script script;
    private List<MatchResult> attempts = new ArrayList<MatchResult>();

    @JsonProperty
    public void setRequest(HttpRequest message) throws IOException {
        request = RequestPattern.fromMessage(message);
    }

    public boolean matches(HttpRequest message) throws URISyntaxException {
        MatchResult result = request.matches(message);
        for (Field field : result.fields) {
            LOGGER.trace("Match outcome: " + field);
        }
        LOGGER.trace("Match score: " + result.score());
        if (result.score() >= 5) { // only record attempts that match request path
            attempts.add(result);
        }
        return result.matches();
    }

    public RequestPattern getRequest() {
        return request;
    }

    public void setRequest(RequestPattern request) {
        this.request = request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    @Override
    public int hashCode() {
        return request.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof StubExchange)
                && ((StubExchange)obj).request.equals(request);
    }    

}
