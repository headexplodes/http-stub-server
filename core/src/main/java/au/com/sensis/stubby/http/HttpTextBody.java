package au.com.sensis.stubby.http;

public class HttpTextBody implements HttpBody {

    private String content;
    
    public HttpTextBody(String content) {
        this.content = content;
    }
    
    @Override
    public HttpBody deepCopy() {
        return new HttpTextBody(content);
    }

    @Override
    public boolean isText() {
        return true;
    }

    @Override
    public boolean isJson() {
        return false;
    }    
    
    @Override
    public String asText() {
        return content;
    }
    
    @Override
    public Object asJson() {
        throw new UnsupportedOperationException();
    }
    
}
