package au.com.sensis.stubby.js;

import au.com.sensis.stubby.StubExchange;
import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.http.HttpResponse;

public class ScriptWorld { // the world as JavaScript sees it

    private HttpRequest request;
    private HttpResponse response;
    private Long delay;

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
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

    // copy everything so script can't change server state
    public static ScriptWorld create(HttpRequest request, StubExchange response) {
        ScriptWorld result = new ScriptWorld();
        result.setRequest(new HttpRequest(request));
        result.setResponse(new HttpResponse(response.getResponse()));
        result.setDelay(response.getDelay());
        return result;
    }
    
}
