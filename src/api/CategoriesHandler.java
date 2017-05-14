package api;

import com.sun.net.httpserver.HttpExchange;
import database.Categories;
import util.APIUtils;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class CategoriesHandler extends BaseHandler {
    public CategoriesHandler(Connection conn) {
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

        String amount = params.get("amount");
        if (amount == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'amount' is required"));
            return;
        }

        String wallet = params.get("wallet");
        if (wallet == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'wallet' is required"));
            return;
        }

        try {
            int id = Categories.createCategory(conn, name, Float.parseFloat(amount), Integer.parseInt(wallet));
            ResultSet rs = Categories.getCategory(conn, id);
            APIUtils.sendResponse(httpExchange, 201, Categories.serialize(rs, false));
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
                ResultSet rs = Categories.getCategory(conn, Integer.parseInt(id));
                if (!rs.isBeforeFirst() ) {
                    APIUtils.sendResponse(httpExchange, 404, format("not found"));
                    return;
                } else {
                    APIUtils.sendResponse(httpExchange, 200, Categories.serialize(rs, false));
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
                ResultSet rs = Categories.getCategoriesOfWallet(conn, Integer.parseInt(wallet));
                APIUtils.sendResponse(httpExchange, 200, Categories.serialize(rs, true));
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
                Categories.updateCategoryName(conn, Integer.parseInt(id), name);
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        String amount = params.get("amount");
        if (amount != null) {
            try {
                Categories.updateCategoryAmount(conn, Integer.parseInt(id), Float.parseFloat(amount));
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        try {
            ResultSet rs = Categories.getCategory(conn, Integer.parseInt(id));
            APIUtils.sendResponse(httpExchange, 200, Categories.serialize(rs, false));
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
            int n = Categories.deleteCategory(conn, Integer.parseInt(id));
            if (n > 0) {
                APIUtils.sendResponse(httpExchange, 200, format("deleted category"));
            } else {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
            }
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
