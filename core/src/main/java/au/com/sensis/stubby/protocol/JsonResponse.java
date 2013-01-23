package au.com.sensis.stubby.protocol;

public class JsonResponse extends JsonMessage {

    private Integer statusCode;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

}
