package views;

import auth.InvalidJsonWebTokenException;
import auth.JsonWebToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import database.Accounts;
import database.Categories;
import database.Movements;
import database.Wallets;
import util.APIUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class MovementsView extends BaseHandler {
    public MovementsView(Connection conn) {
        super(conn);
    }

    @Override
    protected void post(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getPOSTparams(httpExchange);
        Headers headers = httpExchange.getRequestHeaders();

        String token = headers.getFirst("Authorization");
        if (token == null) {
            APIUtils.sendResponse(httpExchange, 401, format("missing authorization header"));
            return;
        }

        String uid = params.get("uid");
        if (uid == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'uid' is required"));
            return;
        }

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
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            int wallet = Categories.getCategoryWallet(conn, Integer.parseInt(category));
            ArrayList<Integer> members = Wallets.getWalletMembers(conn, wallet);
            if (!jwt.isAccessToken() || !members.contains(jwt.getUid())) {
                APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                return;
            }

            int id = Movements.createMovement(conn, name, description, Float.parseFloat(amount), Integer.parseInt(category));
            ResultSet rs = Movements.getMovement(conn, id);
            APIUtils.sendResponse(httpExchange, 201, Movements.serialize(rs, false));
        } catch (InvalidJsonWebTokenException e) {
            APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 400, format(e.getMessage()));
        }
    }

    @Override
    protected void get(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getGETparams(httpExchange);
        Headers headers = httpExchange.getRequestHeaders();

        String token = headers.getFirst("Authorization");
        if (token == null) {
            APIUtils.sendResponse(httpExchange, 401, format("missing authorization header"));
            return;
        }

        String uid = params.get("uid");
        if (uid == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'uid' is required"));
            return;
        }

        String id = params.get("id");
        if (id != null) {
            try {
                PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
                JsonWebToken jwt = new JsonWebToken(token, pub);

                int wallet = Movements.getMovementWallet(conn, Integer.parseInt(id));
                if (wallet == -1) {
                    APIUtils.sendResponse(httpExchange, 404, format("not found"));
                    return;
                }

                ArrayList<Integer> members = Wallets.getWalletMembers(conn, wallet);
                if (!jwt.isAccessToken() || !members.contains(jwt.getUid())) {
                    APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                    return;
                }

                ResultSet rs = Movements.getMovement(conn, Integer.parseInt(id));
                if (!rs.isBeforeFirst()) {
                    APIUtils.sendResponse(httpExchange, 404, format("not found"));
                    return;
                } else {
                    APIUtils.sendResponse(httpExchange, 200, Movements.serialize(rs, false));
                    return;
                }
            } catch (InvalidJsonWebTokenException e) {
                APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
                return;
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            } catch (Exception e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        String wallet = params.get("wallet");
        if (wallet != null) {
            try {
                PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
                JsonWebToken jwt = new JsonWebToken(token, pub);

                ArrayList<Integer> members = Wallets.getWalletMembers(conn, Integer.parseInt(wallet));
                if (!jwt.isAccessToken() || !members.contains(jwt.getUid())) {
                    APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                    return;
                }

                ResultSet rs = Movements.getMovementsOfWallet(conn, Integer.parseInt(wallet));
                APIUtils.sendResponse(httpExchange, 200, Movements.serialize(rs, true));
                return;
            } catch (InvalidJsonWebTokenException e) {
                APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
                return;
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            } catch (Exception e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        APIUtils.sendResponse(httpExchange, 400, format("field 'id' or 'wallet' is required"));
    }

    @Override
    protected void put(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getPUTparams(httpExchange);
        Headers headers = httpExchange.getRequestHeaders();

        String token = headers.getFirst("Authorization");
        if (token == null) {
            APIUtils.sendResponse(httpExchange, 401, format("missing authorization header"));
            return;
        }

        String uid = params.get("uid");
        if (uid == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'uid' is required"));
            return;
        }

        String id = params.get("id");
        if (id == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'id' is required"));
            return;
        }

        try {
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            int wallet = Movements.getMovementWallet(conn, Integer.parseInt(id));
            if (wallet == -1) {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
                return;
            }

            ArrayList<Integer> members = Wallets.getWalletMembers(conn, wallet);
            if (!jwt.isAccessToken() || !members.contains(jwt.getUid())) {
                APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                return;
            }
        } catch (InvalidJsonWebTokenException e) {
            APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
            return;
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 404, format("not found"));
            return;
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
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
        Headers headers = httpExchange.getRequestHeaders();

        String token = headers.getFirst("Authorization");
        if (token == null) {
            APIUtils.sendResponse(httpExchange, 401, format("missing authorization header"));
            return;
        }

        String uid = params.get("uid");
        if (uid == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'uid' is required"));
            return;
        }

        String id = params.get("id");
        if (id == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'id' is required"));
            return;
        }

        try {
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            int wallet = Movements.getMovementWallet(conn, Integer.parseInt(id));
            if (wallet == -1) {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
                return;
            }

            ArrayList<Integer> members = Wallets.getWalletMembers(conn, wallet);
            if (!jwt.isAccessToken() || !members.contains(jwt.getUid())) {
                APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                return;
            }
        } catch (InvalidJsonWebTokenException e) {
            APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
            return;
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 404, format("not found"));
            return;
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
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
