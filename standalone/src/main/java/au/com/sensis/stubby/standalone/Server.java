package au.com.sensis.stubby.standalone;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import au.com.sensis.stubby.HttpResponse;
import au.com.sensis.stubby.NotFoundException;
import au.com.sensis.stubby.StubService;
import au.com.sensis.stubby.StubbedResponse;
import au.com.sensis.stubby.utils.Pair;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Server implements HttpHandler {
    
    private static final Logger LOGGER = Logger.getLogger(Server.class);

    private StubService service = new StubService();
    
    private ObjectMapper mapper() {
        return new ObjectMapper();
    }

    public void handle(HttpExchange exchange) {
        long start = System.currentTimeMillis();
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.startsWith("/_control/")) {
                handleControl(exchange);
            } else {
                handleMatch(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
            try {
                returnError(exchange, e.getMessage());
            } catch (IOException ex) {
                LOGGER.error(ex);
                ex.printStackTrace();
            }
        } finally {
            exchange.close();
        }
        LOGGER.info("Server handle processing time(ms): " + (System.currentTimeMillis() - start));
    }
        
    private void handleMatch(HttpExchange exchange) throws Exception {
        Pair<HttpResponse,Long> pair = service.findMatch(Transformer.fromExchange(exchange));
        if (pair != null) {
            Long delay = pair.getSecond();
            if (delay != null && delay > 0) {
                LOGGER.info("Delayed request, sleeping for " + delay + " ms...");
                Thread.sleep(delay);
            }
            Transformer.populateExchange(pair.getFirst(), exchange);
        } else {
            returnNotFound(exchange, "No stubbed method matched request");
        }
    }
        
    private void handleControl(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Pattern pattern = Pattern.compile("^/_control/(.+?)(/(\\d+))?$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches()) {
            String object = matcher.group(1);
            String indexStr = matcher.group(3);
            if (indexStr != null) {
                int index = Integer.parseInt(indexStr);
                if (object.equals("requests")) {
                    handleRequest(exchange, index);
                } else if (object.equals("responses")) {
                    handleResponse(exchange, index);
                } else {
                    throw new RuntimeException("Unknown object: " + object);
                }
            } else {
                if (object.equals("requests")) {
                    handleRequests(exchange);
                } else if (object.equals("responses")) {
                    handleResponses(exchange);
                } else {
                    throw new RuntimeException("Unknown object: " + object);
                }
            }
        }
    }
    
    private <T> T parseJsonBody(HttpExchange exchange, Class<T> bodyClass) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType != null && contentType.startsWith("application/json")) {
            return mapper().readValue(exchange.getRequestBody(), bodyClass);
        } else {
            throw new RuntimeException("Unexpected content type");
        }
    }
    
    private void returnOk(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatus.SC_OK, -1); // no body
        exchange.getResponseBody().close();
    }
    
    private void returnNotFound(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(HttpStatus.SC_NOT_FOUND, 0); // unknown body length
        exchange.getResponseBody().write(message.getBytes());
        exchange.getResponseBody().close();
    }
    
    private void returnError(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(HttpStatus.SC_INTERNAL_SERVER_ERROR, 0); // unknown body length
        exchange.getResponseBody().write(message.getBytes());
        exchange.getResponseBody().close();
    }
    
    private void returnJson(HttpExchange exchange, Object model) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(HttpStatus.SC_OK, 0); // unknown body length
        mapper().writeValue(exchange.getResponseBody(), model);
        exchange.getResponseBody().close();
    }
            
    private void handleResponses(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("POST")) {
            StubbedResponse stubbedResponse = parseJsonBody(exchange, StubbedResponse.class);
            service.addResponse(stubbedResponse);
            returnOk(exchange);
        } else if (method.equals("DELETE")) {
            service.deleteResponses();
            returnOk(exchange);
        } else if (method.equals("GET")) {
            returnJson(exchange, service.getResponses());           
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }
    
    private void handleResponse(HttpExchange exchange, int index) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            try {
                returnJson(exchange, service.getResponse(index));
            } catch (NotFoundException e) {
                returnNotFound(exchange, e.getMessage());
            }   
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }

    private void handleRequests(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("DELETE")) {
            service.deleteRequests();
            returnOk(exchange);
        } else if (method.equals("GET")) {
            returnJson(exchange, service.getRequests());   
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }
    
    private void handleRequest(HttpExchange exchange, int index) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            try {
                returnJson(exchange, service.getRequest(index));
            } catch (NotFoundException e) {
                returnNotFound(exchange, e.getMessage());
            }   
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }

}
