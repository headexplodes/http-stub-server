package au.com.sensis.stubby;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import au.com.sensis.stubby.utils.MessageUtils;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HttpResponse extends HttpMessage {

    private Integer statusCode;

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
