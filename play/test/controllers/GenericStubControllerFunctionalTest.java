package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class GenericStubControllerFunctionalTest extends FunctionalTest {

    private ObjectMapper mapper = new ObjectMapper();
    
    public class TestParam {
        
        public String name;
        public String value;
        
        public TestParam(String name, String value) {
            this.name = name;
            this.value = value;
        }
                
    }
    
    public class TestHttpMessage {

        public String method;
        public String path; 
        public Integer statusCode;
        public String contentType;
        public String body;
        public List<TestParam> queryParams = new ArrayList<TestParam>();
        public List<TestParam> headers = new ArrayList<TestParam>();
    
    }
    
    public class TestStubMessage {
        
        public TestHttpMessage request = new TestHttpMessage();
        public TestHttpMessage response = new TestHttpMessage();
        
    }
    
    @Before
    public void reset() {
        POST("/_reset");
    }
    
    @Test
    public void testLastNotFound() {
        Response response = GET("/_last");
        assertStatus(404, response);
    }
    
    @Test
    public void testLast() throws Exception {
        GET("/testing?a=1");
        
        Response response = GET("/_last");
        assertStatus(200, response);
        
        String body = getContent(response);
        TestHttpMessage lastMessage = mapper.readValue(body, TestHttpMessage.class);
        Assert.assertEquals("/testing", lastMessage.path);
        Assert.assertEquals(1, lastMessage.queryParams.size());
        Assert.assertEquals("a", lastMessage.queryParams.get(0).name);
        Assert.assertEquals("1", lastMessage.queryParams.get(0).value);
    }
    
    @Test
    public void testStub() throws Exception {
        TestStubMessage message = new TestStubMessage();
        
        message.request.method = "POST";
        message.request.path = "/foo/bar";
        message.request.contentType = "application/json";
        message.request.queryParams.add(new TestParam("a", "1"));
        message.request.queryParams.add(new TestParam("a", "2"));
        message.request.queryParams.add(new TestParam("b", "3"));
        message.request.headers.add(new TestParam("X-Header", "Foo"));
        message.request.headers.add(new TestParam("x-header", "Bar"));
        
        message.response.statusCode = 402;
        message.response.contentType = "text/plain";
        message.response.body = "pay money";
        message.response.headers.add(new TestParam("X-Currency", "dollars"));
     
        Response stubResponse = POST("/_stub", "application/json", mapper.writeValueAsString(message));
        assertStatus(200, stubResponse);
        
        Request request = newRequest();
        request.headers.put("X-Header", new Http.Header("X-Header", Arrays.asList("Bar", "Foo")));
        Response response = POST(request, "/foo/bar?a=1&a=2&b=3", "application/json", "{}");
        
        assertStatus(402, response);
        assertEquals("text/plain", response.contentType);
        assertEquals("pay money", getContent(response));
        assertEquals("dollars", response.getHeader("X-Currency"));
    }
    
}
