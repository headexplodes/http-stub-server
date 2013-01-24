package au.com.sensis.stubby.http;

import org.codehaus.jackson.annotate.JsonIgnore;

import au.com.sensis.stubby.utils.HttpMessageUtils;

public class HttpResponse extends HttpMessage {

    private Integer status;
    
    public HttpResponse() { }
    
    public HttpResponse(HttpResponse other) {
        super(other);
        this.status = other.status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonIgnore
    public String getReasonPhrase() {
        return HttpMessageUtils.getReasonPhrase(status);
    }

}
