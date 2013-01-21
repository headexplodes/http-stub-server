package au.com.sensis.stubby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import au.com.sensis.stubby.utils.MessageUtils;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public abstract class HttpMessage implements Serializable {

    private static final long serialVersionUID = 1L; // don't care

    public static final String H_CONTENT_TYPE = "content-type";

    private static final Logger LOGGER = Logger.getLogger(HttpMessage.class);

    private Object body; // String (Raw) or Map/Collection (JSON)
    private Map<String, HttpHeader> headers = new HashMap<String, HttpHeader>();

    private void addHeader(String name, String value) {
        name = name.toLowerCase(); // header names are always lower-case
        HttpHeader header = headers.get(name);
        if (header == null) {
            header = new HttpHeader();
            header.setName(name);
            headers.put(name, header);
        }
        header.getValues().add(value);
    }
    
    private void setHeader(String name, String value) {
        name = name.toLowerCase(); // header names are always lower-case
        headers.remove(name); // remove first
        addHeader(name, value);
    }

    @JsonProperty
    public void setHeaders(List<FlatParam> flattened) {
        for (FlatParam header : flattened) {
            addHeader(header.name, header.value);
        }
    }

    @JsonProperty
    public List<FlatParam> getHeaders() {
        List<FlatParam> result = new ArrayList<FlatParam>();
        for (HttpHeader header : headers.values()) {
            result.addAll(header.flatten());
        }
        return result;
    }

    @JsonIgnore
    public boolean isJson() {
        String contentType = getContentType();
        return contentType != null && contentType.startsWith("application/json");
    }

    @JsonIgnore
    public Object bodyAsJson() {
        if (body instanceof String) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(body.toString(), Object.class); // support object or array as top-level
            } catch (Exception e) {
                throw new RuntimeException("Error attempting to parse JSON content", e);
            }
        } else {
            return body; // assume already parsed
        }
    }

    @JsonIgnore
    public String getFormattedBody() {
        if (isJson()) {
            ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
            try {
                return writer.writeValueAsString(bodyAsJson()); // write pretty
            } catch (Exception e) {
                LOGGER.debug("Error attempting to write JSON content", e); // don't really care
            }
        }
        return body.toString(); // if all else fails, return as-is.
    }

    @JsonIgnore
    public List<FlatParam> getHeadersPretty() { // un-lower-case them
        List<FlatParam> result = new ArrayList<FlatParam>();
        for (FlatParam header : getHeaders()) {
            result.add(new FlatParam(MessageUtils.upperCaseHeader(header.name), header.value));
        }
        return result;
    }

    @JsonIgnore
    public Map<String, HttpHeader> getHeadersMap() {
        return headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @JsonProperty
    public String getContentType() {
        HttpHeader header = headers.get(H_CONTENT_TYPE);
        if (header != null) {
            return header.getValues().get(0); // assume only one value
        } else {
            return null; // header not found
        }
    }

    @JsonProperty
    public void setContentType(String contentType) {
        setHeader(H_CONTENT_TYPE, contentType);
    }

}
