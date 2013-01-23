package au.com.sensis.stubby;

import java.util.List;
import java.util.NoSuchElementException;

import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.protocol.JsonExchange;

public class ProtocolServiceInterface {

    private StubService service;
    private ProtocolTransformer transformer;

    public ProtocolServiceInterface(StubService service) {
        this.service = service;
        this.transformer = new ProtocolTransformer();
    }

    public void addResponse(JsonExchange exchange) {
        service.addResponse(transformer.toStubExchange(exchange));
    }

    public JsonExchange getResponse(int index) {
        return transformer.toJsonExchange(service.getResponse(index));
    }

    public List<JsonExchange> getResponses() {
        List<JsonExchange> 
        return responses;
    }

    public void deleteResponse(int index) throws NotFoundException {
        responses.remove(index);
    }

    public void deleteResponses() {
        responses.clear();
    }

    public HttpRequest getRequest(int index) throws NotFoundException {
        return requests.get(index);

    }

    public List<HttpRequest> getRequests() {
        return requests;
    }

    public void deleteRequest(int index) throws NotFoundException {
        requests.remove(index);
    }

    public synchronized void deleteRequests() {
        requests.clear();
    }
}
