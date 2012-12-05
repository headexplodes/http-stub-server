package au.com.sensis.stubby.test;

import java.util.ArrayList;

public class MessageBuilder {

    private Client client;

    public MessageBuilder(Client client) {
        this.client = client;
    }

    public static class Pair {
        public String name;
        public String value;

        public Pair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    @SuppressWarnings("serial") // only for testing anyway...
    public static class PairList extends ArrayList<Pair> {
        public void setPair(String name, String value) {
            for (Pair header : this) {
                if (header.name.equalsIgnoreCase(name)) {
                    header.value = value;
                    return;
                }
            }
            addPair(name, value);
        }

        public void addPair(String name, String value) {
            add(new Pair(name, value));
        }
    }

    public static abstract class AbstractMessage {
        public PairList headers;
        public Object body;

        public void setHeader(String name, String value) {
            if (headers == null) {
                headers = new PairList();
            }
            headers.setPair(name, value);
        }

        public void addHeader(String name, String value) {
            if (headers == null) {
                headers = new PairList();
            }
            headers.addPair(name, value);
        }
    }

    public static class Request extends AbstractMessage {
        public String path;
        public String method;
        public PairList queryParams;

        public void setQueryParam(String name, String value) {
            if (queryParams == null) {
                queryParams = new PairList();
            }
            queryParams.setPair(name, value);
        }

        public void addQueryParam(String name, String value) {
            if (queryParams == null) {
                queryParams = new PairList();
            }
            queryParams.addPair(name, value);
        }
    }

    public static class Response extends AbstractMessage {
        public int statusCode;
    }

    public Request request = new Request();
    public Response response = new Response();
    public Long delay;

    public MessageBuilder delay(Long delay) {
        this.delay = delay;
        return this;
    }
    
    public MessageBuilder delete() {
        return method("DELETE");
    }

    public MessageBuilder method(String method) {
        request.method = method;
        return this;
    }

    public MessageBuilder path(String path) {
        request.path = path;
        return this;
    }

    public MessageBuilder status(Integer code) {
        response.statusCode = code;
        return this;
    }

    public MessageBuilder body(Object body) {
        response.body = body;
        return this;
    }

    public MessageBuilder query(String name, String value) {
        request.setQueryParam(name, value);
        return this;
    }

    public MessageBuilder addQuery(String name, String value) {
        request.addQueryParam(name, value);
        return this;
    }

    public MessageBuilder requestHeader(String name, String value) {
        request.setHeader(name, value);
        return this;
    }

    public MessageBuilder addRequestHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    public MessageBuilder responseHeader(String name, String value) {
        response.setHeader(name, value);
        return this;
    }

    public MessageBuilder addResponseHeader(String name, String value) {
        response.addHeader(name, value);
        return this;
    }

    public MessageBuilder stub() {
        client.postMessage(this); 
        return this;
    }

}
