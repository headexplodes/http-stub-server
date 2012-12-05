package au.com.sensis.stubby;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import au.com.sensis.stubby.MatchResult.Field;
import au.com.sensis.stubby.MatchResult.FieldType;

public class RequestPattern {
    
    public static final Pattern DEFAULT_PATTERN = Pattern.compile(".*");
    
    private Pattern method = DEFAULT_PATTERN;
    private Pattern path = DEFAULT_PATTERN;
    private Set<ParamPattern> params = new HashSet<ParamPattern>();
    private Set<ParamPattern> headers = new HashSet<ParamPattern>();
    private BodyPattern body = new EmptyBodyPattern();

    // TODO:
    // if body field is "body:jsonpath", use JsonPathBodyPattern
    // if body field is "body:xpath" [ ... ]
      
    public static RequestPattern fromMessage(HttpRequest message) throws IOException {
        RequestPattern result = new RequestPattern();
        if (message.getMethod() != null) {
            result.method = Pattern.compile(message.getMethod());
        }
        if (message.getPath() != null) {
            result.path = Pattern.compile(message.getPath());
        }
        if (message.getParams() != null) {
            for (HttpParam param : message.getParams().values()) {
                for (String value : param.getValues()) {
                    result.params.add(new ParamPattern(param.getName(), Pattern.compile(value)));
                }
            }
        }
        if (message.getHeadersMap() != null) {
            for (HttpHeader header : message.getHeadersMap().values()) {
                for (String value : header.getValues()) {
                    result.headers.add(new ParamPattern(header.getName(), Pattern.compile(value)));
                }
            }
        }
        if (message.getBody() != null) {
            if (message.getBody() instanceof String) { // assume regular expression
                result.body = new TextBodyPattern(message.getBody().toString());
            } else { // simple JSON body pattern
                result.body = new JsonBodyPattern(message.bodyAsJson());
            }
            // TODO: support JSONPath body as well...
        }
        return result;
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
        String headerName = pattern.name.toLowerCase(); // headers are always lower-cased
        Field field = new Field(FieldType.HEADER, headerName, pattern.pattern);
        HttpHeader header = message.getHeadersMap().get(headerName); 
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

    public Pattern getPath() {
        return path;
    }

    public Set<ParamPattern> getParams() {
        return params;
    }

    public Set<ParamPattern> getHeaders() {
        return headers;
    }

    /*
    public BodyPattern getBody() {
        return body;
    }
    */
}
