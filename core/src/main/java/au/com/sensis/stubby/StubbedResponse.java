package au.com.sensis.stubby;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import au.com.sensis.stubby.MatchResult.Field;

public class StubbedResponse implements Serializable {

    private static final long serialVersionUID = -1l;

    private static final Logger LOGGER = Logger.getLogger(StubbedResponse.class);

    private transient RequestPattern request = new RequestPattern();
    private HttpResponse response = new HttpResponse();
    private Long delay; // milliseconds
    private String script; //JavaScript statement to execute
    
    private transient List<MatchResult> attempts = new ArrayList<MatchResult>();

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

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public int hashCode() {
        return request.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof StubbedResponse)
                && ((StubbedResponse)obj).request.equals(request);
    }    

}
