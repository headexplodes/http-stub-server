package au.com.sensis.stubby.model.test;

import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;

public class StubRequestBuilder extends StubMessageBuilder<StubRequestBuilder> { // for unit test data setup

    private StubRequest request;

    public StubRequestBuilder() {
        super(new StubRequest());
        this.request = (StubRequest) super.message;
    }

    public StubRequest withParam(String name, String value) {
        request.getParams().add(new StubParam(name, value));
        return request;
    }

    public StubRequest withMethod(String method) {
        request.setMethod(method);
        return request;
    }

    public StubRequest withPath(String path) {
        request.setPath(path);
        return request;
    }

}
