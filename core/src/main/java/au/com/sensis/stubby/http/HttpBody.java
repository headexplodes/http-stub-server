package au.com.sensis.stubby.http;

public interface HttpBody {

    public HttpBody deepCopy();
    
    public boolean isText();
    
    public boolean isJson();
    
    public String asText();
    
    public Object asJson();
    
}
