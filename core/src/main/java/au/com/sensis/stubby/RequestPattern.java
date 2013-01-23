package au.com.sensis.stubby;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import au.com.sensis.stubby.MatchResult.Field;
import au.com.sensis.stubby.MatchResult.FieldType;
import au.com.sensis.stubby.http.HttpMessage;
import au.com.sensis.stubby.http.HttpParam;
import au.com.sensis.stubby.http.HttpRequest;

public class RequestPattern {
    
    public static final Pattern DEFAULT_PATTERN = Pattern.compile(".*");
    
    private Pattern method;
    private Pattern path;
    private Set<ParamPattern> params;
    private Set<ParamPattern> headers;
    private BodyPattern body;

    // TODO:
    // if body field is "body:jsonpath", use JsonPathBodyPattern
    // if body field is "body:xpath" [ ... ]
    
    public RequestPattern() { 
        this.method = DEFAULT_PATTERN;
        this.path = DEFAULT_PATTERN;
        this.params = new HashSet<ParamPattern>();
        this.headers = new HashSet<ParamPattern>();
        this.body = new EmptyBodyPattern();
    }
    
    public RequestPattern(RequestPattern other) { // copy constructor
        this.method = other.method;
        this.path = other.path;
        this.params = new HashSet<ParamPattern>();
        for (ParamPattern param : other.params) {
            this.params.add(new ParamPattern(param));
        }
        this.headers = new HashSet<ParamPattern>();
        for (ParamPattern header : other.headers) {
            this.headers.add(new ParamPattern(header));
        }
        this.body = other.body; // assume this is immutable
    }

    public MatchResult matches(HttpRequest message) throws URISyntaxException {
        MatchResult result = new MatchResult();
        
        Field methodField = new Field(FieldType.METHOD, "method", method, message.getMethod());
        if (method != null) {
            if (method.matcher(message.getMethod()).matches()) {
                result.add(methodField.asMatch(message.getMethod()));
            } else {
                result.add(methodField.asMatchFailure(message.getMethod()));
            }
        }
        
        Field pathField = new Field(FieldType.PATH, "path", path, message.getPath());
        if (path.matcher(message.getPath()).matches()) {
            result.add(pathField.asMatch(message.getPath()));
        } else {
            result.add(pathField.asMatchFailure(message.getPath()));
        }
        
        for (ParamPattern paramPattern : params) {
            result.add(matchParam(message, paramPattern));
        }
        
        for (ParamPattern headerPattern : headers) {
            result.add(matchHeader(message, headerPattern));
        }
        
        if (body != null) {
            Field bodyField = new Field(FieldType.BODY, "body", body.expectedValue());
            if (body.matches(message)) {
                result.add(bodyField.asMatch(message.getBody()));
            } else {
                result.add(bodyField.asMatchFailure(message.getBody()));
            }
        }
        
        return result;
    }
    
    private Field matchParam(HttpRequest message, ParamPattern pattern) {
        Field field = new Field(FieldType.QUERY_PARAM, pattern.name, pattern.pattern);
        HttpParam param = message.getParams().get(pattern.name);
        if (param != null) {
            for (String value : param.getValues()) {
                if (pattern.pattern.matcher(value).matches()) {
                    return field.asMatch(value);
                }
            }
            return field.asMatchFailure(param.getValues().toString());
        } else {
            return field.asNotFound();
        }
    }
    
    private Field matchHeader(HttpMessage message, ParamPattern pattern) {
        Field field = new Field(FieldType.HEADER, pattern.name, pattern.pattern);
        HttpParam header = message.getHeadersMap().get(pattern.name); 
        if (header != null) {
            for (String value : header.getValues()) {
                if (pattern.pattern.matcher(value).matches()) {
                    return field.asMatch(value);
                }
            }
            return field.asMatchFailure(header.getValues().toString());
        } else {
            return field.asNotFound();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((params == null) ? 0 : params.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ParamPattern)
                && ((RequestPattern)obj).method.pattern().equals(method.pattern())
                && ((RequestPattern)obj).path.pattern().equals(path.pattern())
                && ((RequestPattern)obj).params.equals(params)
                && ((RequestPattern)obj).headers.equals(headers)
                && ((RequestPattern)obj).body.equals(body);
    }

    public Pattern getMethod() {
        return method;
    }

    public void setMethod(Pattern method) {
        this.method = method;
    }

    public Pattern getPath() {
        return path;
    }

    public void setPath(Pattern path) {
        this.path = path;
    }

    public BodyPattern getBody() {
        return body;
    }

    public void setBody(BodyPattern body) {
        this.body = body;
    }

    public Set<ParamPattern> getParams() {
        return params;
    }

    public Set<ParamPattern> getHeaders() {
        return headers;
    }

}
