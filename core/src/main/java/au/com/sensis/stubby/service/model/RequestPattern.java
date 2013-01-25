package au.com.sensis.stubby.service.model;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import au.com.sensis.stubby.model.StubMessage;
import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.service.model.MatchResult.Field;
import au.com.sensis.stubby.service.model.MatchResult.FieldType;

public class RequestPattern {

    public static final Pattern DEFAULT_PATTERN = Pattern.compile(".*");

    private Pattern method;
    private Pattern path;
    private Set<ParamPattern> params;
    private Set<ParamPattern> headers;
    private BodyPattern body;

    public RequestPattern(StubRequest request) {
        this.method = toPattern(request.getMethod());
        this.path = toPattern(request.getPath());
        this.params = toPattern(request.getParams());
        this.headers = toPattern(request.getHeaders());
        this.body = toBodyPattern(request.getBody());
    }
    
    private Pattern toPattern(String value) {
        return (value != null) ? DEFAULT_PATTERN : Pattern.compile(value);
    }
    
    private Set<ParamPattern> toPattern(List<StubParam> params) {
        Set<ParamPattern> pattern = new HashSet<ParamPattern>();
        if (params != null) {
            for (StubParam param : params) {
                pattern.add(new ParamPattern(param.getName(), toPattern(param.getValue())));
            }
        }
        return pattern;
    }
    
    private BodyPattern toBodyPattern(Object object) {
        if (object != null) {
            if (object instanceof String) {
                return new TextBodyPattern(object.toString());
            } else if (object instanceof Map
                    || object instanceof List) {
                return new JsonBodyPattern(object);
            } else {
                throw new RuntimeException("Unexpected body type: " + object);
            }
        } else {
            return new EmptyBodyPattern();
        }
    }

    public MatchResult matches(StubRequest message) throws URISyntaxException {
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

    private Field matchParam(StubRequest message, ParamPattern pattern) {
        Field field = new Field(FieldType.QUERY_PARAM, pattern.getName(), pattern.getPattern());
        List<String> values = message.getParams(pattern.getName());
        if (!values.isEmpty()) {
            for (String value : values) {
                if (pattern.getPattern().matcher(value).matches()) {
                    return field.asMatch(value);
                }
            }
            return field.asMatchFailure(values.toString());
        } else {
            return field.asNotFound();
        }
    }

    private Field matchHeader(StubMessage message, ParamPattern pattern) {
        Field field = new Field(FieldType.HEADER, pattern.getName(), pattern.getPattern());
        List<String> values = message.getHeaders(pattern.getName()); // case insensitive lookup
        if (!values.isEmpty()) {
            for (String value : values) {
                if (pattern.getPattern().matcher(value).matches()) {
                    return field.asMatch(value);
                }
            }
            return field.asMatchFailure(values.toString());
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
                && ((RequestPattern) obj).method.pattern().equals(method.pattern())
                && ((RequestPattern) obj).path.pattern().equals(path.pattern())
                && ((RequestPattern) obj).params.equals(params)
                && ((RequestPattern) obj).headers.equals(headers)
                && ((RequestPattern) obj).body.equals(body);
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
