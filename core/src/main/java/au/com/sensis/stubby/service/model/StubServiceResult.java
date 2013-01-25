package au.com.sensis.stubby.service.model;

import au.com.sensis.stubby.model.StubResponse;

public class StubServiceResult { // returned by the 'findMatch' method

    private boolean matchFound;
    private StubResponse response;
    private Long delay;

    public StubServiceResult() {
        this.matchFound = false;
    }

    public StubServiceResult(StubResponse response, Long delay) {
        this.matchFound = true;
        this.response = response;
        this.delay = delay;
    }

    public boolean matchFound() {
        return matchFound;
    }

    public StubResponse getResponse() {
        return response;
    }

    public Long getDelay() {
        return delay;
    }

}
