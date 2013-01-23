package au.com.sensis.stubby;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import au.com.sensis.stubby.protocol.JsonExchange;
import au.com.sensis.stubby.utils.JsonUtils;

public class JsonServiceInterface {

    private ProtocolServiceInterface service;
    private ObjectMapper mapper;

    public JsonServiceInterface(ProtocolServiceInterface service) {
        this.service = service;
        this.mapper = JsonUtils.defaultMapper();
    }
       
    public void addResponse(JsonExchange exchange) {
        service.addResponse(new ProtocolTransformer().toStubExchange(exchange)); // protocol => internal representation
    }

    public void addResponse(String responseJson) {
        try {
            addResponse(mapper.readValue(responseJson, JsonExchange.class));
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
    
    
}
