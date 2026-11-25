package com.xyz.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Tiny web app: shows which pod answered and which build is running. */
public class App {

    static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            return "unknown";
        }
    }

    static String page(String host, String version) {
        return "<html><body style=\"font-family:sans-serif;text-align:center;margin-top:60px\">"
                + "<h1>Hello from Arpitha - CI/CD works</h1>"
                + "<p>Served by pod: <b>" + host + "</b></p>"
                + "<p>Build version: <b>" + version + "</b></p>"
                + "</body></html>";
    }

    static void respond(HttpExchange ex, int code, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        String version = System.getenv().getOrDefault("APP_VERSION", "local");

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", ex -> respond(ex, 200, "text/html; charset=utf-8", page(hostname(), version)));
        server.createContext("/health", ex -> respond(ex, 200, "text/plain", "OK"));
        server.start();
        System.out.println("xyz-app listening on port " + port);
    }
}
