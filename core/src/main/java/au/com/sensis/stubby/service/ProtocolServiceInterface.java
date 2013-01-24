package au.com.sensis.stubby.service;

import java.util.LinkedList;
import java.util.List;

import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.protocol.JsonExchange;
import au.com.sensis.stubby.protocol.JsonRequest;
import au.com.sensis.stubby.protocol.ProtocolTransformer;
import au.com.sensis.stubby.service.model.StubExchange;

public class ProtocolServiceInterface { // transform between protocol classes and internal classes

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
        List<JsonExchange> result = new LinkedList<JsonExchange>();
        for (StubExchange exchange : service.getResponses()) {
            result.add(transformer.toJsonExchange(exchange));
        }
        return result;
    }

    public void deleteResponse(int index) throws NotFoundException {
        service.deleteResponse(index);
    }

    public void deleteResponses() {
        service.deleteResponses();
    }

    public JsonRequest getRequest(int index) throws NotFoundException {
        return transformer.toJsonRequest(service.getRequest(index));
    }

    public List<JsonRequest> getRequests() {
        List<JsonRequest> result = new LinkedList<JsonRequest>();
        for (HttpRequest request : service.getRequests()) {
            result.add(transformer.toJsonRequest(request));
        }
        return result;
    }

    public void deleteRequest(int index) throws NotFoundException {
        service.deleteRequest(index);
    }

    public void deleteRequests() {
        service.deleteRequests();
    }
    
}
