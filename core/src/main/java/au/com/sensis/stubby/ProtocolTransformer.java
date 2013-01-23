package au.com.sensis.stubby;

import java.util.ArrayList;
import java.util.regex.Pattern;

import au.com.sensis.stubby.http.HttpBody;
import au.com.sensis.stubby.http.HttpJsonBody;
import au.com.sensis.stubby.http.HttpResponse;
import au.com.sensis.stubby.http.HttpTextBody;
import au.com.sensis.stubby.js.Script;
import au.com.sensis.stubby.protocol.JsonExchange;
import au.com.sensis.stubby.protocol.JsonParam;
import au.com.sensis.stubby.protocol.JsonRequest;
import au.com.sensis.stubby.protocol.JsonResponse;

public class ProtocolTransformer {

    public StubExchange toStubExchange(JsonExchange message) {
        StubExchange result = new StubExchange();
        result.setRequest(toRequestPattern(message.getRequest()));
        result.setResponse(toHttpResponse(message.getResponse()));
        result.setDelay(message.getDelay());
        result.setScript(toScript(message.getScript()));
        return result;
    }
    
    public JsonExchange toJsonExchange(StubExchange exchange) {
        JsonExchange result = new JsonExchange();
        result.setRequest(toJsonRequest(exchange.getRequest()));
        result.setResponse(toJsonResponse(exchange.getResponse()));
        result.setDelay(exchange.getDelay());
        result.setScript(toString(exchange.getScript()));
        return result;
    }
    
    private Script toScript(String source) {
        if (source != null) {
            return new Script(source);
        } else {
            return null; // no script
        }
    }
    
    private String toString(Script script) {
        if (script != null) {
            return script.getSource();
        } else {
            return null; // no script
        }
    }
    
    private ParamPattern toParamPattern(JsonParam message) {
        return new ParamPattern(message.getName(), Pattern.compile(message.getValue()));
    }
    
    private JsonParam toJsonParam(ParamPattern pattern) {
        return new JsonParam(pattern.getName(), pattern.getPattern().pattern());
    }
    
    private JsonParam toJsonParam(FlatParam param) {
        return new JsonParam(param.getName(), param.getValue());
    }
    
    private RequestPattern toRequestPattern(JsonRequest message) {
        if (message != null) {
            RequestPattern result = new RequestPattern();
            if (message.getMethod() != null) {
                result.setMethod(Pattern.compile(message.getMethod()));
            }
            if (message.getPath() != null) {
                result.setPath(Pattern.compile(message.getPath()));
            }
            if (message.getQueryParams() != null) {
                for (JsonParam param : message.getQueryParams()) {
                    result.getParams().add(toParamPattern(param));
                }
            }
            if (message.getHeaders() != null) {
                for (JsonParam param : message.getHeaders()) {
                    result.getHeaders().add(toParamPattern(param));
                }
            }
            if (message.getBody() != null) {
                if (message.getBody() instanceof String) {
                    result.setBody(new TextBodyPattern(message.getBody().toString()));
                } else {
                    result.setBody(new JsonBodyPattern(message.getBody()));
                }
            }
            return result;
        } else {
            throw new RuntimeException("Missing 'request' field");
        }
    }
    
    private JsonRequest toJsonRequest(RequestPattern pattern) {
        JsonRequest result = new JsonRequest();
        if (pattern.getMethod() != null) {
            result.setMethod(pattern.getMethod().pattern());
        }
        if (pattern.getPath() != null) {
            result.setPath(pattern.getPath().pattern());
        }
        if (pattern.getParams() != null) {
            result.setQueryParams(new ArrayList<JsonParam>());
            for (ParamPattern param : pattern.getParams()) {
                result.getQueryParams().add(toJsonParam(param));
            }
        }
        if (pattern.getHeaders() != null) {
            result.setHeaders(new ArrayList<JsonParam>());
            for (ParamPattern param : pattern.getHeaders()) {
                result.getHeaders().add(toJsonParam(param));
            }
        }
        if (pattern.getBody() != null) {
            BodyPattern body = pattern.getBody();
            if (body instanceof TextBodyPattern) {
                result.setBody(((TextBodyPattern)body).getPattern().pattern());
            } else if (body instanceof JsonBodyPattern){
                result.setBody(((JsonBodyPattern)body).getPattern());
            } else if (body instanceof EmptyBodyPattern) {
                result.setBody(null); // no body
            } else {
                throw new RuntimeException("Unknown body pattern type");
            }
        }
        return result;
    }
    
    private HttpResponse toHttpResponse(JsonResponse message) {
        if (message != null) {
            HttpResponse result = new HttpResponse();
            if (message.getStatusCode() != null) {
                result.setStatusCode(message.getStatusCode());
            } else {
                throw new RuntimeException("Missing 'request.statusCode' field");
            }
            if (message.getHeaders() != null) {
                for (JsonParam param : message.getHeaders()) {
                    result.addHeader(param.getName(), param.getValue());
                }
            }
            if (message.getBody() != null) {
                if (message.getBody() instanceof String) {
                    result.setBody(new HttpTextBody(message.getBody().toString()));
                } else {
                    result.setBody(new HttpJsonBody(message.getBody()));
                }
            }
            return result;
        } else {
            throw new RuntimeException("Missing 'response' field");
        }
    }
    
    private JsonResponse toJsonResponse(HttpResponse response) {
        JsonResponse result = new JsonResponse();
        result.setStatusCode(response.getStatusCode());
        if (response.getHeaders() != null) {
            result.setHeaders(new ArrayList<JsonParam>());
            for (FlatParam param : response.getHeaders()) {
                result.getHeaders().add(toJsonParam(param));
            }
        }
        if (response.getBody() != null) {
            HttpBody body = response.getBody();
            if (body.isText()) {
                result.setBody(body.asText());
            } else if (body.isJson()) {
                result.setBody(body.asJson());
            } else {
                throw new RuntimeException("Unknown body type");
            }
        }
        return result;
    }

}
