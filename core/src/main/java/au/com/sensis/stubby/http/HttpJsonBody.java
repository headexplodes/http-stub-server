package au.com.sensis.stubby.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HttpJsonBody implements HttpBody {

    private Object content;

    public HttpJsonBody(Object content) {
        this.content = content;
    }

    @Override
    public HttpBody deepCopy() { // assume Jackon creates serializable object graph
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            new ObjectOutputStream(outStream).writeObject(this);
            
            ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
            Object copied = new ObjectInputStream(inStream).readObject();
            
            return new HttpJsonBody(copied);
        } catch (Exception e) {
            throw new RuntimeException("Error performing deep clone", e);
        }
    }

    @Override
    public boolean isText() {
        return false;
    }

    @Override
    public boolean isJson() {
        return true;
    }
    
    @Override
    public String asText() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object asJson() {
        return content;
    }

}
