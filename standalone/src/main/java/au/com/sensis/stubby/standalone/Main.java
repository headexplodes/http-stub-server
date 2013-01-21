package au.com.sensis.stubby.standalone;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    
    private static final int SOCKET_BACKLOG = 10;

    public static void main(String[] args) throws Exception {
        final List<HttpServer> servers = new ArrayList<HttpServer>();
        final Server server = new Server(); // share between protocols
        if (args.length >= 1) { // HTTP server
            HttpServer httpServer = startHttpServer(Integer.parseInt(args[0]), server);
            LOGGER.info("Started HTTP server on " + httpServer.getAddress());
            servers.add(httpServer);
        }
        if (args.length >= 2) { // HTTPS server
            HttpServer httpsServer = startHttpsServer(Integer.parseInt(args[1]), server);
            LOGGER.info("Started HTTPS server on " + httpsServer.getAddress());
            servers.add(httpsServer);
        }
        if (args.length > 3) {
            throw new RuntimeException("Usage: java ... <http_port> [<https_port>]");
        }
        
        Thread shutdownHook = new Thread() {
            public void run() {
                LOGGER.info("Stopping servers...");
                for (HttpServer server : servers) {
                    server.stop(1);
                }
            }
        };
        
        server.setShutdownHook(shutdownHook); // handle shutdown requests over HTTP
        Runtime.getRuntime().addShutdownHook(shutdownHook); // handle SIGINT etc.
    }

    private static HttpServer startHttpServer(int port, Server server) throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), SOCKET_BACKLOG);
        httpServer.createContext("/", server);
        httpServer.start();
        return httpServer;
    }

    private static HttpsServer startHttpsServer(int port, Server server) throws Exception {
        String[] properties = { // also see 'javax.net.debug' for debugging
                "javax.net.ssl.keyStore", 
                "javax.net.ssl.keyStorePassword" };
        
        for (String property : properties) {
            if (System.getProperty(property) == null) {
                LOGGER.warn("Should set property '" + property + "' when using HTTPS connector");
            }
        }

        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(port), SOCKET_BACKLOG);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));
        httpsServer.createContext("/", server);
        httpsServer.start();
        return httpsServer;
    }

}
