package au.com.sensis.stubby.standalone;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

public class Main {

    private static final int SOCKET_BACKLOG = 10;

    public static void main(String[] args) throws Exception {
        final List<HttpServer> servers = new ArrayList<HttpServer>();
        if (args.length >= 1) { // HTTP server
            HttpServer server = startHttpServer(Integer.parseInt(args[0]));
            System.out.println("Started HTTP server on " + server.getAddress());
            servers.add(server);
        }
        if (args.length >= 2) { // HTTPS server
            HttpServer server = startHttpsServer(Integer.parseInt(args[1]));
            System.out.println("Started HTTPS server on " + server.getAddress());
            servers.add(server);
        }
        if (args.length > 3) {
            throw new RuntimeException("Usage: java ... <http_port> [<https_port>]");
        }
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    public void run() {
                        System.out.println("Stopping servers...");
                        for (HttpServer server : servers) {
                            server.stop(1);
                        }
                    }
                });
    }

    private static HttpServer startHttpServer(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), SOCKET_BACKLOG);
        server.createContext("/", new Server());
        server.start();
        return server;
    }

    private static HttpsServer startHttpsServer(int port) throws Exception {
        String[] properties = { // also see 'javax.net.debug' for debugging
                "javax.net.ssl.keyStore", 
                "javax.net.ssl.keyStorePassword" };
        for (String property : properties) {
            if (System.getProperty(property) == null) {
                System.out.println("[Warning] Should set property: " + property);
            }
        }

        HttpsServer server = HttpsServer.create(new InetSocketAddress(port), SOCKET_BACKLOG);
        server.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));
        server.createContext("/", new Server());
        server.start();
        return server;
    }

}
