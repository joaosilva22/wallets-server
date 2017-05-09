package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.APIUtils;

import java.io.IOException;
import java.sql.Connection;

public class BaseHandler implements HttpHandler {
    protected Connection conn;

    protected BaseHandler(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "POST":
                post(httpExchange);
                break;
            case "GET":
                get(httpExchange);
                break;
            case "PUT":
                put(httpExchange);
                break;
            case "DELETE":
                delete(httpExchange);
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
}
