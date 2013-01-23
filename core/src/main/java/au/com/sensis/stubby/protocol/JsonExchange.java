package au.com.sensis.stubby.protocol;

public class JsonExchange {

    private JsonRequest request;
    private JsonResponse response;
    private Long delay;
    private String script;

    public JsonRequest getRequest() {
        return request;
    }

    public void setRequest(JsonRequest request) {
        this.request = request;
    }

    public JsonResponse getResponse() {
        return response;
    }

    public void setResponse(JsonResponse response) {
        this.response = response;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

}
