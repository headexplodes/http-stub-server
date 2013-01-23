package au.com.sensis.stubby;

import au.com.sensis.stubby.http.HttpMessage;

public final class EmptyBodyPattern extends BodyPattern { 
    
    @Override
    public boolean matches(HttpMessage request) {
        return true; // always passes
    }
    
    @Override
    public String expectedValue() {
        return "<anything>";
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof EmptyBodyPattern);
    }
    
    @Override
    public int hashCode() {
        return 1;
    }
    
}
