package api;

import com.sun.net.httpserver.HttpExchange;
import database.Movements;
import util.APIUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MovementsHandler extends BaseHandler {
    public MovementsHandler(Connection conn) {
        super(conn);
    }

    @Override
    protected void post(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getPOSTparams(httpExchange);

        String name = params.get("name");
        if (name == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'name' is required"));
            return;
        }

        String description = params.get("description");
        if (description == null) {
            description = "";
        }

        String amount = params.get("amount");
        if (amount == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'amount' is required"));
            return;
        }

        String category = params.get("category");
        if (category == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'category' is required"));
            return;
        }

        try {
            int id = Movements.createMovement(conn, name, description, Float.parseFloat(amount), Integer.parseInt(category));
            ResultSet rs = Movements.getMovement(conn, id);
            APIUtils.sendResponse(httpExchange, 201, Movements.serialize(rs, false));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }

    @Override
    protected void get(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getGETparams(httpExchange);

        String id = params.get("id");
        if (id != null) {
            try {
                ResultSet rs = Movements.getMovement(conn, Integer.parseInt(id));
                if (!rs.isBeforeFirst()) {
                    APIUtils.sendResponse(httpExchange, 404, format("not found"));
                    return;
                } else {
                    APIUtils.sendResponse(httpExchange, 200, Movements.serialize(rs, false));
                    return;
                }
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        String wallet = params.get("wallet");
        if (wallet != null) {
            try {
                ResultSet rs = Movements.getMovementsOfWallet(conn, Integer.parseInt(wallet));
                APIUtils.sendResponse(httpExchange, 200, Movements.serialize(rs, true));
                return;
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        APIUtils.sendResponse(httpExchange, 400, format("field 'id' or 'wallet' is required"));
    }

    @Override
    protected void put(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getPUTparams(httpExchange);

        String id = params.get("id");
        if (id == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'id' is required"));
            return;
        }

        String name = params.get("name");
        if (name != null) {
            try {
                Movements.updateMovementName(conn, Integer.parseInt(id), name);
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        String description = params.get("description");
        if (description != null) {
            try {
                Movements.updateMovementDescription(conn, Integer.parseInt(id), description);
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        String amount = params.get("amount");
        if (amount != null) {
            try {
                Movements.updateMovementAmount(conn, Integer.parseInt(id), Float.parseFloat(amount));
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        String category = params.get("category");
        if (amount != null) {
            try {
                Movements.updateMovementCategory(conn, Integer.parseInt(id), Integer.parseInt(category));
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        try {
            ResultSet rs = Movements.getMovement(conn, Integer.parseInt(id));
            APIUtils.sendResponse(httpExchange, 200, Movements.serialize(rs, false));
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
            return;
        }

        try {
            int n = Movements.deleteMovement(conn, Integer.parseInt(id));
            if (n > 0) {
                APIUtils.sendResponse(httpExchange, 200, format("deleted movement"));
            } else {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
            }
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
