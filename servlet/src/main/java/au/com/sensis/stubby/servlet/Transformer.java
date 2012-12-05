package au.com.sensis.stubby.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import au.com.sensis.stubby.HttpHeader;
import au.com.sensis.stubby.HttpParam;
import au.com.sensis.stubby.HttpRequest;
import au.com.sensis.stubby.HttpResponse;

/*
 * Transform between stubby & Servlet HTTP structures
 */
public class Transformer {

    @SuppressWarnings("unchecked")
    public static List<HttpHeader> fromServletHeaders(HttpServletRequest request) {
        List<HttpHeader> result = new ArrayList<HttpHeader>();
        Enumeration<String> headers = (Enumeration<String>)request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            Enumeration<String> headerValues = (Enumeration<String>)request.getHeaders(headerName);
            HttpHeader header = new HttpHeader();
            header.setName(headerName.toLowerCase()); // all header names should be lower-cased
            while (headerValues.hasMoreElements()) {
                header.getValues().add(headerValues.nextElement()); 
            }
            result.add(header);
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public static List<HttpParam> fromServletParams(HttpServletRequest request) {
        List<HttpParam> result = new ArrayList<HttpParam>();
        Enumeration<String> params = (Enumeration<String>)request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            HttpParam param = new HttpParam();
            param.setName(paramName);
            for (String value : request.getParameterValues(paramName)) {
                param.getValues().add(value);
            }
            result.add(param);
        }
        return result;
    }

    public static HttpRequest fromServletRequest(HttpServletRequest request) {
        HttpRequest result = new HttpRequest();
        try {
            result.setPath(new URI(request.getRequestURL().toString()).getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String method = request.getMethod().toUpperCase(); // method should always be upper-case
        result.setMethod(method); 
        if (!method.equals("GET")) { // GET requests don't (shouldn't) have a body or content type
            try {
                result.setBody(IOUtils.toString(request.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (HttpParam param : fromServletParams(request)) {
            result.getParams().put(param.getName(), param);
        }
        for (HttpHeader header : fromServletHeaders(request)) {
            result.getHeadersMap().put(header.getName(), header);
        }
        return result;
    }

    public static void populateServletResponse(HttpResponse message, HttpServletResponse response) throws IOException {
        for (HttpHeader header : message.getHeadersMap().values()) {
            for (String value : header.getValues()) {
                response.setHeader(header.getName(), value);
            }
        }
        response.setStatus(message.getStatusCode());
        if (message.getBody() instanceof String) {
            IOUtils.write(message.getBody().toString(), response.getOutputStream());
        } else {
            new ObjectMapper().writeValue(response.getOutputStream(), message.getBody()); // assume deserialised JSON (ie, a Map) 
        }
    }

}
