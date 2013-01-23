package au.com.sensis.stubby.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import au.com.sensis.stubby.http.HttpMessage;

public class MessageUtils {
    
    private static final Logger LOGGER = Logger.getLogger(MessageUtils.class);

    public static String getReasonPhrase(int statusCode) {
        switch (statusCode) {
        case 200:
            return "OK";
        case 201:
            return "Created";
        case 202:
            return "Accepted";
        case 301:
            return "Moved Permanently";
        case 302:
            return "Found";
        case 304:
            return "Not Modified";
        case 400:
            return "Bad Request";
        case 401:
            return "Unauthorized";
        case 403:
            return "Forbidden";
        case 404:
            return "Not Found";
        case 406:
            return "Not Acceptable";
        case 415:
            return "Unsupported Media Type";
        case 422:
            return "Unprocessable Entity";
        case 500:
            return "Internal Server Error";
        case 503:
            return "Service Unavailable";
        default:
            return null;
        }
    }
        
    public static String upperCaseHeader(String name) {
        Pattern pattern = Pattern.compile("\\-.|^.");
        Matcher matcher = pattern.matcher(name);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group().toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    @JsonIgnore
    public String formatBody(HttpMessage message) {
        String contentType = message.getContentType();
        Object body = message.getBody();
        if (contentType != null && contentType.startsWith("application/json")) {
            ObjectMapper mapper = JsonUtils.defaultMapper();
            ObjectWriter writer = JsonUtils.prettyWriter();
            if (message.getBody() instanceof String) {
                try {
                    return writer.writeValueAsString(mapper.readValue(body.toString(), Map.class)); // read-and-write pretty
                } catch (Exception e) {
                    LOGGER.debug("Error attempting to parse JSON content", e); // might be malformed
                }
            } else {
                try {
                    return writer.writeValueAsString(body); // write pretty
                } catch (Exception e) {
                    LOGGER.debug("Error attempting to write JSON content", e); // don't really care
                }
            }
        }
        return body.toString(); // if all else fails, return as-is.
    }
    
}
