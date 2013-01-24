package au.com.sensis.stubby.protocol;

public class JsonResponse extends JsonMessage {

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
