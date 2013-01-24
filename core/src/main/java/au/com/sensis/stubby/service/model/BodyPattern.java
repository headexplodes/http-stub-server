package au.com.sensis.stubby.service.model;

import au.com.sensis.stubby.model.StubMessage;

// all instances should be imutable (so we can safely pass them to scripts etc.)
public abstract class BodyPattern { // Note: using abstract class so we can force override of equals()
    
    public abstract boolean matches(StubMessage request);
    
    public abstract String expectedValue(); // TODO: if matches() returns a 'MatchResult' of some kind, this may not be needed.
    
    @Override
    public abstract boolean equals(Object object); // force implementors to override
    
    @Override
    public abstract int hashCode(); // force implementors to override

}
