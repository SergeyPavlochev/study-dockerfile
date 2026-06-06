package ru.spavlochev;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Set;

public class SimpleHttpServer {

    private static final int PORT = 8000;
    private static final String SUPPORTED_METHOD = "GET";
    private static final Set<String> SUPPORTED_URIS = Set.of("/health", "/health/");
    private static final byte[] DEFAULT_RESPONSE = "{\"status\": \"OK\"}".getBytes(Charset.defaultCharset());
    private static final int STATUS_OK = 200;
    private static final int STATUS_NOT_FOUND = 404;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/", httpExchange -> {
            String requestMethod = httpExchange.getRequestMethod();
            String requestURI = httpExchange.getRequestURI().toString();
            System.out.printf("Incoming request: method %s, URI '%s'%n", requestMethod, requestURI);

            if (isSupportedRequest(requestMethod, requestURI)) {
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(STATUS_OK, DEFAULT_RESPONSE.length);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(DEFAULT_RESPONSE);
                }
                System.out.println("Supported request: response status " + STATUS_OK);
            } else {
                httpExchange.sendResponseHeaders(STATUS_NOT_FOUND, -1);
                System.out.println("Unsupported request: response status " + STATUS_NOT_FOUND);
            }
            httpExchange.close();
        });

        httpServer.start();

        System.out.println("Simple Server has been started at Port " + PORT);
    }

    private static boolean isSupportedRequest(String requestMethod, String requestURI) {
        return SUPPORTED_METHOD.equals(requestMethod) &&
                SUPPORTED_URIS.contains(requestURI);
    }
}
