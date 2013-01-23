package au.com.sensis.stubby;

import java.util.regex.Pattern;

import au.com.sensis.stubby.http.HttpMessage;

public class TextBodyPattern extends BodyPattern {
        
    private static final Pattern TEXT_CONTENT_TYPE = Pattern.compile("text/.*");
    
    private Pattern pattern;
    
    public TextBodyPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
    
    private boolean isText(HttpMessage request) {
        String contentType = request.getContentType();
        return contentType != null 
                && TEXT_CONTENT_TYPE.matcher(contentType).matches();
    }

    @Override
    public String expectedValue() {
        return "m/" + pattern.pattern() + "/";
    }
    
    @Override
    public boolean matches(HttpMessage request) {
        if (isText(request) && request.getBody() != null) { // require a body
            return pattern.matcher(request.getBody().toString()).matches(); // match pattern against entire body
        } else {
            return false; // expected textual content
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TextBodyPattern)
                && ((TextBodyPattern)obj).pattern.pattern().equals(pattern.pattern());
    }
    
    @Override
    public int hashCode() {
        return pattern.pattern().hashCode();
    }

    public Pattern getPattern() {
        return pattern;
    }

}
