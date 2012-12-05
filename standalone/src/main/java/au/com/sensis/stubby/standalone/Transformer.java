package au.com.sensis.stubby.standalone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.codehaus.jackson.map.ObjectMapper;

import au.com.sensis.stubby.HttpHeader;
import au.com.sensis.stubby.HttpParam;
import au.com.sensis.stubby.HttpRequest;
import au.com.sensis.stubby.HttpResponse;

import com.sun.net.httpserver.HttpExchange;

/*
 * Transform between stubby & HttpExchange structures
 */
public class Transformer {

    public static List<HttpHeader> fromExchangeHeaders(HttpExchange exchange) {
        List<HttpHeader> result = new ArrayList<HttpHeader>();
        for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
            String headerName = entry.getKey();
            HttpHeader header = new HttpHeader();
            header.setName(headerName.toLowerCase()); // all header names should be lower-cased
            for (String value : entry.getValue()) {
                header.getValues().add(value); 
            }
            result.add(header);
        }
        return result;
    }

    public static List<HttpParam> fromExchangeParams(HttpExchange exchange) {
        List<NameValuePair> params = URLEncodedUtils.parse(exchange.getRequestURI(), "UTF-8");
        Map<String,HttpParam> result = new HashMap<String,HttpParam>();
        for (NameValuePair pair : params) {
            HttpParam param = result.get(pair.getName());
            if (param == null) {
                param = new HttpParam();
                param.setName(pair.getName());
                result.put(pair.getName(), param);
            }
            param.getValues().add(pair.getValue() == null ? "" : pair.getValue());
        }
        return new ArrayList<HttpParam>(result.values());
    }

    public static HttpRequest fromExchange(HttpExchange exchange) {
        HttpRequest result = new HttpRequest();
        result.setPath(exchange.getRequestURI().getPath());
        String method = exchange.getRequestMethod().toUpperCase(); // method should always be upper-case
        result.setMethod(method);
        if (!method.equals("GET")) { // GET requests don't (shouldn't) have a body or content type
            try {
                result.setBody(IOUtils.toString(exchange.getRequestBody()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (HttpParam param : fromExchangeParams(exchange)) {
            result.getParams().put(param.getName(), param);
        }
        for (HttpHeader header : fromExchangeHeaders(exchange)) {
            result.getHeadersMap().put(header.getName(), header);
        }
        return result;
    }

    public static void populateExchange(HttpResponse message, HttpExchange exchange) throws IOException {
        for (HttpHeader header : message.getHeadersMap().values()) {
            for (String value : header.getValues()) {
                exchange.getResponseHeaders().add(header.getName(), value);
            }
        }
        exchange.sendResponseHeaders(message.getStatusCode(), 0); // arbirary-length body
        if (message.getBody() instanceof String) {
            IOUtils.write(message.getBody().toString(), exchange.getResponseBody());
        } else {
            new ObjectMapper().writeValue(exchange.getResponseBody(), message.getBody()); // assume deserialised JSON (ie, a Map) 
        }
        exchange.getResponseBody().close();
    }

}
