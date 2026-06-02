package ru.spavlochev;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleHttpServer {

	private static final Logger LOG = Logger.getLogger(SimpleHttpServer.class.getName());
	private static final byte[] DEFAULT_RESPONSE = "{\"status\": \"OK\"}".getBytes(Charset.defaultCharset());
	private static final int OK = 200;
	public static final int NOT_FOUND = 404;
	private static final int PORT = 8000;

	public static void main(String[] args) throws IOException {
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

		httpServer.createContext("/", httpExchange -> {
			String requestMethod = httpExchange.getRequestMethod();
			String requestURI = httpExchange.getRequestURI().toString();
			LOG.log(Level.INFO, "IN_Rq: Method {0}, URI {1}", new Object[]{requestMethod, requestURI});

			if (isValidRequest(requestMethod, requestURI)) {
				Headers responseHeaders = httpExchange.getResponseHeaders();
				responseHeaders.add("Content-Type", "application/json");
				httpExchange.sendResponseHeaders(OK, DEFAULT_RESPONSE.length);
				try (OutputStream outputStream = httpExchange.getResponseBody()) {
					outputStream.write(DEFAULT_RESPONSE);
				}
				LOG.log(Level.INFO, "OUT_Rs: URI {0}, Status {1}, Body {2}",
						new Object[]{requestURI, OK, new String(DEFAULT_RESPONSE)});
			} else {
				LOG.warning("Unsupported URI '" + requestURI + "'");
				httpExchange.sendResponseHeaders(NOT_FOUND, -1);
				LOG.log(Level.INFO, "OUT_Rs: URI {0}, Status {1}", new Object[]{requestURI, NOT_FOUND});
			}

			httpExchange.close();
		});

		httpServer.start();

		LOG.info("Simple Server has been started at Port " + PORT);
		LOG.info("Attention! Simple Server works with exactly one endpoint: '/health'. " +
				"Requests passed to other endpoints are not supported.");
	}

	private static boolean isValidRequest(String requestMethod, String requestURI) {
		return "GET".equals(requestMethod) &&
				("/health".equals(requestURI) || "/health/".equals(requestURI));
	}
}
