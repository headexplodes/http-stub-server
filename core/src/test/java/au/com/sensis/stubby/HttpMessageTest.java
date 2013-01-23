package au.com.sensis.stubby;

import java.util.Arrays;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.stubby.http.HttpRequest;
import au.com.sensis.stubby.http.HttpResponse;
import au.com.sensis.stubby.utils.JsonUtils;

public class HttpMessageTest {

    private ObjectMapper mapper = JsonUtils.defaultMapper();
    
    @Test
    public void testDeserialisePath() throws Exception {
        HttpRequest message = mapper.readValue("{\"path\":\"/foo\"}", HttpRequest.class);
        Assert.assertEquals("/foo", message.getPath());
    }
    
    @Test
    public void testDeserialiseMethod() throws Exception {
        HttpRequest message = mapper.readValue("{\"method\":\"FOO\"}", HttpRequest.class);
        Assert.assertEquals("FOO", message.getMethod());
    }
    
    @Test
    public void testDeserialiseStatusCode() throws Exception {
        HttpResponse message = mapper.readValue("{\"statusCode\":400}", HttpResponse.class);
        Assert.assertEquals(new Integer(400), message.getStatusCode());
    }
    
    @Test
    public void testDeserialiseContentType() throws Exception {
        HttpRequest message = mapper.readValue("{\"contentType\":\"foo/bar\"}", HttpRequest.class);
        Assert.assertEquals("foo/bar", message.getContentType());
    }
    
    @Test
    public void testDeserialiseBody() throws Exception {
        HttpRequest message = mapper.readValue("{\"body\":\"bar\"}", HttpRequest.class);
        Assert.assertEquals("bar", message.getBody());
    }
    
    @Test
    public void testDeserialiseParams() throws Exception {
        HttpRequest message = mapper.readValue("{\"queryParams\":[{\"name\":\"foo\",\"value\":\"bar\"}]}", HttpRequest.class);
        Assert.assertEquals(1, message.getParams().size());
        Assert.assertEquals("foo", message.getParams().get("foo").getName());
        Assert.assertEquals(Arrays.asList("bar"), message.getParams().get("foo").getValues());
    }
    
    @Test
    public void testDeserializeHeaders() throws Exception {
        HttpRequest message = mapper.readValue("{\"headers\":[{\"name\":\"X-Foo\",\"value\":\"Bar\"}]}", HttpRequest.class);
        Assert.assertEquals(1, message.getHeadersMap().size());
        Assert.assertEquals("x-foo", message.getHeadersMap().get("x-foo").getName());
        Assert.assertEquals(Arrays.asList("Bar"), message.getHeadersMap().get("x-foo").getValues());
    }
    
}
