package au.com.sensis.stubby.standalone;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsExchange;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class HttpsTest {
    
  public static void main(String[] args) throws Exception {
    KeyStore ks = KeyStore.getInstance("JKS");  
    ks.load(HttpsTest.class.getResourceAsStream("/ssl_cert.jks"), "password".toCharArray());

    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, "password".toCharArray());

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(kmf.getKeyManagers(), null, null);

    final HttpsServer server = HttpsServer.create(new InetSocketAddress("localhost", 8443), 10);

    server.createContext("/", new HttpHandler() {
      public void handle(HttpExchange xchng) throws IOException {
          System.out.println("Got request");
          
          try {
          
        HttpsExchange exchange = (HttpsExchange) xchng;

        /*
        String ret = "";
        ret += exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getProtocol() + "\n";

        Headers headers = exchange.getRequestHeaders();
        if (!headers.isEmpty()) {
          ret += "\n";
          for (String key : headers.keySet()) {
            ret += key + ": ";
            boolean semiColon = false;
            for (String value : headers.get(key)) {
              if (semiColon) {
                ret += "; ";
              }

              ret += value;
              semiColon = true;
            }

            ret += "\n";
          }
        }

        if (headers.get("Content-Length") != null) {
          InputStream in = exchange.getRequestBody();
          ret += "\n";
          int i;
          while ((i = in.read()) != -1) {
            ret += String.valueOf((char) i);
          }
        }
        */

        //headers = exchange.getResponseHeaders();
        //headers.set("Content-Type", "text/plain");

        System.out.println(">> " + IOUtils.toString(exchange.getRequestBody()));
        
        String ret = "success";
        
        exchange.sendResponseHeaders(200, ret.getBytes().length);

        OutputStream out = exchange.getResponseBody();
        out.write(ret.getBytes());

        exchange.close();
        
          } catch (Throwable t) {
              t.printStackTrace();
              throw new RuntimeException(t);
          }
      }
    });

    server.setHttpsConfigurator(new HttpsConfigurator(context) {
      public void configure(HttpsParameters params) {

      }
    });

    server.setExecutor(new Executor() {
      public void execute(Runnable command) {
        new Thread(command).start();
      }
    });

    server.start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        server.stop(0);
      }
    });
    
  }
}