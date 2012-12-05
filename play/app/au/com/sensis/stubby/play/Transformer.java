package au.com.sensis.stubby.play;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Http;
import play.mvc.Scope;
import au.com.sensis.stubby.HttpHeader;
import au.com.sensis.stubby.HttpMessage;
import au.com.sensis.stubby.HttpParam;

/*
 * Transform between stubby & Play! HTTP structures
 */
public class Transformer {

    public static List<HttpHeader> fromPlayHeaders(Map<String, Http.Header> headers) {
        List<HttpHeader> result = new ArrayList<HttpHeader>();
        for (Http.Header playHeader : headers.values()) {
            HttpHeader header = new HttpHeader();
            header.name = playHeader.name.toLowerCase(); // Play already does this, but just to be sure.
            header.values = playHeader.values;
            result.add(header);
        }
        return result;
    }
    
    public static List<HttpParam> fromPlayParams(Scope.Params params) {
        List<HttpParam> result = new ArrayList<HttpParam>();
        for (Map.Entry<String,String[]> entry : params.all().entrySet()) {
            if (entry.getKey().equals("body") // Play! puts the request body in this
                    || entry.getKey().equals("_ignore")) { // created by 'catch all' route
                continue;
            }
            HttpParam param = new HttpParam();
            param.name = entry.getKey();
            param.values = Arrays.asList(entry.getValue());
            result.add(param);
        }
        return result;
    }

    public static HttpRequest fromPlayRequest(Http.Request request) {
        HttpRequest result = new HttpRequest();
        try {
            result.path = new URI(request.url).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        result.method = request.method.toUpperCase();
        if (!result.method.equals("GET")) { // GET requests don't (shouldn't) have a body or content type
            result.contentType = request.contentType;
            try {
                result.body = IOUtils.toString(request.body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (HttpParam param : fromPlayParams(request.params)) {
            result.params.put(param.name, param);
        }
        for (HttpHeader header : fromPlayHeaders(request.headers)) {
            result.headers.put(header.name, header);
        }
        return result;
    }

    public static void populatePlayResponse(HttpMessage message, Http.Response response) throws IOException {
        response.contentType = message.contentType;
        response.status = message.statusCode;
        if (message.body instanceof String) {
            response.print(message.body);
        } else {
            response.print(new ObjectMapper().writeValueAsString(message.body)); // assume deserialised JSON (ie, a Map) 
        }
        for (HttpHeader header : message.headers.values()) {
            for (String value : header.values) {
                Http.Header playHeader = response.headers.get(header.name);
                if (playHeader != null) {
                    playHeader.values.add(value);
                } else {
                    response.setHeader(header.name, value);
                }
            }
        }
    }

}
