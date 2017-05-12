package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.APIUtils;
import util.IOUtils;

import java.io.IOException;
import java.sql.Connection;

public class BaseHandler implements HttpHandler {
    protected Connection conn;

    protected static final String ERR_404_NOT_FOUND = "{ \"error\": \"resource not found\" }";
    protected static final String ERR_500_INTERNAL_SERVER_ERROR = "{ \"error\": \"internal server error\" }";

    protected BaseHandler(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        switch (method) {
            case "POST":
                post(httpExchange);
                IOUtils.log("POST " + path + " " + httpExchange.getResponseCode());
                break;
            case "GET":
                get(httpExchange);
                IOUtils.log("GET " + path + " " + httpExchange.getResponseCode());
                break;
            case "PUT":
                put(httpExchange);
                IOUtils.log("PUT " + path + " " + httpExchange.getResponseCode());
                break;
            case "DELETE":
                delete(httpExchange);
                IOUtils.log("DELETE " + path + " " + httpExchange.getResponseCode());
                break;
            default:
                APIUtils.sendResponse(httpExchange, 405, "Method not allowed");
        }
    }

    protected void post(HttpExchange httpExchange) throws IOException {
        APIUtils.sendResponse(httpExchange, 405, "Method not allowed");
    }

    protected void get(HttpExchange httpExchange) throws IOException {
        APIUtils.sendResponse(httpExchange, 405, "Method not allowed");
    }

    protected void put(HttpExchange httpExchange) throws IOException {
        APIUtils.sendResponse(httpExchange, 405, "Method not allowed");
    }

    protected void delete(HttpExchange httpExchange) throws IOException {
        APIUtils.sendResponse(httpExchange, 405, "Method not allowed");
    }

    protected String format(String message) {
        return "{ \"message\": \"" + message + "\" }";
    }
}
