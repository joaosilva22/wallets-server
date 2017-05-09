package api;

import com.sun.net.httpserver.HttpExchange;
import database.Wallets;
import util.APIUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class WalletsHandler extends BaseHandler {
    public WalletsHandler(Connection conn) {
        super(conn);
    }

    @Override
    protected void post(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getPOSTparams(httpExchange);

        String name = params.get("name");
        if (name == null) {
            APIUtils.sendResponse(httpExchange, 400, "Field 'name' is required");
        }

        String owner = params.get("owner");
        if (owner == null) {
            APIUtils.sendResponse(httpExchange, 400, "Field 'owner' is required");
        }

        try {
            Wallets.createWallet(conn, name, Integer.parseInt(owner));
            APIUtils.sendResponse(httpExchange, 201, "Wallet created successfully");
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, e.getMessage());
        }
    }
}
