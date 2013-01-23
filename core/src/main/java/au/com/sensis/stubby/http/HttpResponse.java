package au.com.sensis.stubby.http;

import org.codehaus.jackson.annotate.JsonIgnore;

import au.com.sensis.stubby.utils.MessageUtils;

public class HttpResponse extends HttpMessage {

    private Integer statusCode;
    
    public HttpResponse() { }
    
    public HttpResponse(HttpResponse other) {
        super(other);
        this.statusCode = other.statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @JsonIgnore
    public String getReasonPhrase() {
        return MessageUtils.getReasonPhrase(statusCode);
    }

}
