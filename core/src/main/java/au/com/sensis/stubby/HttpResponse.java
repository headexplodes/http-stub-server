package au.com.sensis.stubby;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import au.com.sensis.stubby.utils.MessageUtils;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HttpResponse extends HttpMessage implements Serializable {

    private static final long serialVersionUID = 1L; // don't care
    
    private Integer statusCode;

    public HttpResponse deepClone() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        HttpResponse cloned = (HttpResponse) ois.readObject();
        return cloned;
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
