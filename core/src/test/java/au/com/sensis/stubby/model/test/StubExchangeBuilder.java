package au.com.sensis.stubby.model.test;

import au.com.sensis.stubby.model.StubExchange;

public class StubExchangeBuilder { // for unit test data setup

    private StubExchange exchange;
    
    public StubExchange get() {
        return exchange;
    }
    
    public StubExchangeBuilder withDelay(Long delay) {
        exchange.setDelay(delay);
        return this;
    }
    
    public StubExchangeBuilder withScript(String script) {
        exchange.setScript(script);
        return this;
    }

}
