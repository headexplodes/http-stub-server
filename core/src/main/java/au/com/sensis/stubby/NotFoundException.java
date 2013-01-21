package au.com.sensis.stubby;

@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L; // don't care
    
    public NotFoundException(String message) {
        super(message);
    }
    
}
