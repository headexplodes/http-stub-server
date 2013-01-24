package au.com.sensis.stubby.service.model;

import au.com.sensis.stubby.model.StubMessage;

public final class EmptyBodyPattern extends BodyPattern { 
    
    @Override
    public boolean matches(StubMessage request) {
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
