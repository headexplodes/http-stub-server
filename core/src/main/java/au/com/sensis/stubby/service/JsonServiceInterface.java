package au.com.sensis.stubby.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.map.ObjectMapper;

import au.com.sensis.stubby.protocol.JsonExchange;
import au.com.sensis.stubby.utils.JsonUtils;

public class JsonServiceInterface { // interface using serialized JSON strings/streams

    private ProtocolServiceInterface service;
    private ObjectMapper mapper;

    public JsonServiceInterface(ProtocolServiceInterface service) {
        this.service = service;
        this.mapper = JsonUtils.defaultMapper();
    }
    
    public void addResponse(String exchange) {
        service.addResponse(deserialize(exchange, JsonExchange.class));
    }
    
    public void addResponse(InputStream stream) {
        service.addResponse(deserialize(stream, JsonExchange.class));
    }

    public String getResponse(int index) {
        return serialize(service.getResponse(index));
    }
    
    public void getResponse(OutputStream stream, int index) {
        serialize(stream, service.getResponse(index));
    }

    public String getResponses() {
        return serialize(service.getResponses());
    }
    
    public void getResponses(OutputStream stream) {
        serialize(stream, service.getResponses());
    }

    public void deleteResponse(int index) throws NotFoundException {
        service.deleteResponse(index);
    }

    public void deleteResponses() {
        service.deleteResponses();
    }

    public String getRequest(int index) throws NotFoundException {
        return serialize(service.getRequest(index));
    }
    
    public void getRequest(OutputStream stream, int index) throws NotFoundException {
        serialize(stream, service.getRequest(index));
    }

    public String getRequests() {
        return serialize(service.getRequests());
    }
    
    public void getRequests(OutputStream stream) {
        serialize(stream, service.getRequests());
    }

    public void deleteRequest(int index) throws NotFoundException {
        service.deleteRequest(index);
    }

    public void deleteRequests() {
        service.deleteRequests();
    }
    
    private String serialize(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing JSON", e);
        }
    }
    
    private void serialize(OutputStream stream, Object object) {
        try {
            mapper.writeValue(stream, object);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing JSON", e);
        }
    }

    private <T> T deserialize(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing JSON", e);
        }
    }
    
    private <T> T deserialize(InputStream stream, Class<T> type) {
        try {
            return mapper.readValue(stream, type);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing JSON", e);
        }
    }

}
