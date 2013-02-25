package au.com.sensis.stubby.test;

import org.apache.http.entity.ContentType;

import au.com.sensis.stubby.test.model.JsonExchange;
import au.com.sensis.stubby.test.model.JsonExchangeList;
import au.com.sensis.stubby.test.model.JsonRequestList;
import au.com.sensis.stubby.utils.JsonUtils;

public class Client extends GenericClient {

    public Client(String baseUri) {
        super(baseUri);
    }

    public void postMessage(JsonExchange message) {
        executePost("/_control/responses", JsonUtils.serialize(message), ContentType.APPLICATION_JSON).assertOk()
                .close();
    }

    public void postMessage(String message) {
        executePost("/_control/responses", message, ContentType.APPLICATION_JSON).assertOk().close();
    }

    public JsonExchangeList getResponses() {
        return executeGet("/_control/responses").assertOk().getJson(JsonExchangeList.class);
    }

    public JsonRequestList getRequests() {
        return executeGet("/_control/requests").assertOk().getJson(JsonRequestList.class);
    }

    public JsonExchangeList getResponse(int index) {
        return executeGet("/_control/responses/" + index).assertOk().getJson(JsonExchangeList.class);
    }

    public JsonRequestList getRequest(int index) {
        return executeGet("/_control/requests/" + index).assertOk().getJson(JsonRequestList.class);
    }

    public void deleteResponses() {
        executeDelete("/_control/responses").assertOk().close();
    }

    public void deleteRequests() {
        executeDelete("/_control/requests").assertOk().close();
    }

    public void reset() {
        deleteResponses();
        deleteRequests();
    }

}