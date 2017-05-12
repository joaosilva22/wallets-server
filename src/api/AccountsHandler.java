package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.Accounts;
import util.APIUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class AccountsHandler extends BaseHandler {
    public AccountsHandler(Connection conn) {
        super(conn);
    }

    @Override
    protected void post(HttpExchange httpExchange) throws IOException {
        try {
            Accounts.createAccount(conn);
            APIUtils.sendResponse(httpExchange, 201, format("account created successfully"));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
