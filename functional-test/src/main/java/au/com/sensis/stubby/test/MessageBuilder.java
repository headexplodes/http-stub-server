package au.com.sensis.stubby.test;

import au.com.sensis.stubby.test.model.JsonExchange;

public class MessageBuilder {

    private Client client;
    private JsonExchange exchange;

    public MessageBuilder(Client client) {
        this.client = client;
        this.exchange = new JsonExchange();
    }
 
    public MessageBuilder delay(Long delay) {
        exchange.delay = delay;
        return this;
    }
    
    public MessageBuilder method(String method) {
        exchange.request.method = method;
        return this;
    }

    public MessageBuilder path(String path) {
        exchange.request.path = path;
        return this;
    }

    public MessageBuilder status(Integer status) {
        exchange.response.status = status;
        return this;
    }

    public MessageBuilder body(Object body) {
        exchange.response.body = body;
        return this;
    }

    public MessageBuilder query(String name, String value) {
        exchange.request.setParam(name, value);
        return this;
    }

    public MessageBuilder addQuery(String name, String value) {
        exchange.request.addParam(name, value);
        return this;
    }

    public MessageBuilder requestHeader(String name, String value) {
        exchange.request.setHeader(name, value);
        return this;
    }

    public MessageBuilder addRequestHeader(String name, String value) {
        exchange.request.addHeader(name, value);
        return this;
    }

    public MessageBuilder responseHeader(String name, String value) {
        exchange.response.setHeader(name, value);
        return this;
    }

    public MessageBuilder addResponseHeader(String name, String value) {
        exchange.response.addHeader(name, value);
        return this;
    }

    public MessageBuilder stub() {
        client.postMessage(exchange); 
        return this;
    }

}
