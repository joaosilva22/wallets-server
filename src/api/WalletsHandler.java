package api;

import com.sun.net.httpserver.HttpExchange;
import database.Wallets;
import util.APIUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
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
            APIUtils.sendResponse(httpExchange, 400, format("field 'name' is required"));
        }

        String owner = params.get("owner");
        if (owner == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'owner' is required"));
        }

        try {
            int id = Wallets.createWallet(conn, name, Integer.parseInt(owner));
            ResultSet rs = Wallets.getWallet(conn, id);
            APIUtils.sendResponse(httpExchange, 201, Wallets.serialize(rs));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }

    @Override
    protected void get(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getGETparams(httpExchange);

        String id = params.get("id");
        if (id == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'id' is required"));
        }

        try {
            ResultSet rs = Wallets.getWallet(conn, Integer.parseInt(id));
            if (!rs.isBeforeFirst() ) {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
            } else {
                APIUtils.sendResponse(httpExchange, 200, Wallets.serialize(rs));
            }
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }

    @Override
    protected void put(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getPUTparams(httpExchange);

        String name = params.get("name");
        if (name == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'name' is required"));
        }

        String id = params.get("id");
        if (id == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'id' is required"));
        }

        try {
            Wallets.updateWallet(conn, name, Integer.parseInt(id));
            ResultSet rs = Wallets.getWallet(conn, Integer.parseInt(id));
            APIUtils.sendResponse(httpExchange, 200, Wallets.serialize(rs));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }

    @Override
    protected void delete(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getDELETEparams(httpExchange);

        String id = params.get("id");
        if (id == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'id' is required"));
        }

        try {
            int n = Wallets.deleteWallet(conn, Integer.parseInt(id));
            if (n > 0) {
                APIUtils.sendResponse(httpExchange, 200, format("deleted wallet"));
            } else {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
            }
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
