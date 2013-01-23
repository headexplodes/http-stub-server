package au.com.sensis.stubby.standalone;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import au.com.sensis.stubby.http.HttpParam;
import au.com.sensis.stubby.http.HttpParamSet;
import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.http.HttpResponse;
import au.com.sensis.stubby.utils.JsonUtils;

import com.sun.net.httpserver.HttpExchange;

/*
 * Transform between stubby & HttpExchange structures
 */
public class Transformer {

    public static HttpParamSet fromExchangeHeaders(HttpExchange exchange) {
        HttpParamSet result = new HttpParamSet();
        for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
            result.add(entry.getKey().toLowerCase(), entry.getValue()); // all header names should be lower-cased
        }
        return result;
    }

    public static HttpParamSet fromExchangeParams(HttpExchange exchange) {
        List<NameValuePair> params = URLEncodedUtils.parse(exchange.getRequestURI(), "UTF-8");
        HttpParamSet result = new HttpParamSet();
        for (NameValuePair pair : params) {
            result.add(pair.getName(), pair.getValue());
        }
        return result;
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
        result.setParams(fromExchangeParams(exchange));
        result.setHeaders(fromExchangeHeaders(exchange));
        return result;
    }

    public static void populateExchange(HttpResponse message, HttpExchange exchange) throws IOException {
        for (HttpParam header : message.getHeadersMap().values()) {
            for (String value : header.getValues()) {
                exchange.getResponseHeaders().add(header.getName(), value);
            }
        }
        exchange.sendResponseHeaders(message.getStatusCode(), 0); // arbirary-length body
        if (message.getBody() instanceof String) {
            IOUtils.write(message.getBody().toString(), exchange.getResponseBody());
        } else {
            new JsonUtils.defaultMapper().writeValue(exchange.getResponseBody(), message.getBody()); // assume deserialised JSON (ie, a Map) 
        }
        exchange.getResponseBody().close();
    }

}
