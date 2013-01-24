package au.com.sensis.stubby.http;

import java.util.List;

import au.com.sensis.stubby.FlatParam;

public abstract class HttpMessage {

    public static final String H_CONTENT_TYPE = "Content-Type";

    //private static final Logger LOGGER = Logger.getLogger(HttpMessage.class);

    //private HttpBody body;
    private String body;
    private HttpParamSet headers;

    protected HttpMessage() { 
        this.headers = new HttpParamSet(true); // create case-insensitive map
    }
    
    protected HttpMessage(HttpMessage other) { // copy constructor
        //this.body = other.body.deepCopy();
        this.body = other.body;
        this.headers = new HttpParamSet(other.headers);
    }
    
    public void addHeader(String name, String value) {
        headers.add(name, value);
    }
    
    public void setHeader(String name, String value) {
        headers.set(name, value);
    }

//    @JsonProperty
//    public void setHeaders(List<FlatParam> flattened) {
//        headers.addAll(flattened);
//    }
//
//    @JsonProperty
    public List<FlatParam> getHeaders() {
        return headers.flatten();
    }

//    public boolean isJson() {
//        String contentType = getContentType();
//        return contentType != null && contentType.startsWith("application/json");
//    }

//    public Object bodyAsJson() {
//        if (body instanceof String) {
//            try {
//                return JsonUtils.defaultMapper().readValue(body.toString(), Object.class); // support object or array as top-level
//            } catch (Exception e) {
//                throw new RuntimeException("Error attempting to parse JSON content", e);
//            }
//        } else {
//            return body; // assume already parsed
//        }
//    }

//    @JsonIgnore
//    public String getFormattedBody() {
//        if (isJson()) {
//            try {
//                return JsonUtils.prettyPrint(bodyAsJson()); // write pretty
//            } catch (Exception e) {
//                LOGGER.debug("Error attempting to write JSON content", e); // don't really care
//            }
//        }
//        return body.toString(); // if all else fails, return as-is.
//    }

//    @JsonIgnore
//    public List<FlatParam> getHeadersPretty() { // un-lower-case them
//        List<FlatParam> result = new ArrayList<FlatParam>();
//        for (FlatParam header : getHeaders()) {
//            result.add(new FlatParam(MessageUtils.upperCaseHeader(header.getName()), header.getValue()));
//        }
//        return result;
//    }

    //@JsonIgnore
//    public Map<String, HttpParam> getHeadersMap() {
//        return headers.getMap();
//    }
    
    public void setHeaders(HttpParamSet headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }    

//    public HttpBody getBody() {
//        return body;
//    }
//
//    public void setBody(Object body) {
//        if (body instanceof String) {
//            this.body = new HttpTextBody((String)body);
//        } else {
//            this.body = new HttpJsonBody(body);
//        }
//    }

//    @JsonProperty
//    public String getContentType() {
//        return headers.getValue(H_CONTENT_TYPE);
//    }
//
//    @JsonProperty
//    public void setContentType(String contentType) {
//        setHeader(H_CONTENT_TYPE, contentType);
//    }

}
