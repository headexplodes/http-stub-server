package au.com.sensis.stubby.service.model;

import java.util.regex.Pattern;

import au.com.sensis.stubby.model.StubMessage;
import au.com.sensis.stubby.utils.HttpMessageUtils;

public class TextBodyPattern extends BodyPattern {

    private Pattern pattern;
    
    public TextBodyPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
    
    @Override
    public String expectedValue() {
        return pattern.pattern();
    }
    
    @Override
    public boolean matches(StubMessage request) {
        if (HttpMessageUtils.isText(request) && request.getBody() != null) { // require a body
            return pattern.matcher(HttpMessageUtils.bodyAsText(request)).matches(); // match pattern against entire body
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
