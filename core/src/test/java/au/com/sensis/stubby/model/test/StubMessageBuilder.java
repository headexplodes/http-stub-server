package au.com.sensis.stubby.model.test;

import au.com.sensis.stubby.model.StubMessage;
import au.com.sensis.stubby.model.StubParam;

public abstract class StubMessageBuilder<T extends StubMessageBuilder<?>> { // for unit test data setup

    protected StubMessage message;

    public StubMessageBuilder(StubMessage message) {
        this.message = message;
    }
    
    public T withBody(Object body) {
        message.setBody(body);
        return (T)this;
    }
    
    public T withHeader(String name, String value) {
        message.getHeaders().add(new StubParam(name, value));
        return (T)this;
    }
        
}
