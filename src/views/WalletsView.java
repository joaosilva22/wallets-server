package views;

import auth.InvalidJsonWebTokenException;
import auth.JsonWebToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import database.Accounts;
import database.Wallets;
import util.APIUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class WalletsView extends BaseView {
    public WalletsView(Connection conn) {
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

        String name = params.get("name");
        if (name == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'name' is required"));
            return;
        }

        String owner = params.get("owner");
        if (owner == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'owner' is required"));
            return;
        }

        try {
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(owner));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            if (!jwt.isAccessToken() || jwt.getUid() != Integer.parseInt(owner)) {
                APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                return;
            }
        } catch (InvalidJsonWebTokenException e) {
            APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
            return;
        } catch(SQLException e) {
            APIUtils.sendResponse(httpExchange, 404, format("not found"));
            return;
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
            return;
        }

        try {
            int id = Wallets.createWallet(conn, name, Integer.parseInt(owner));
            ResultSet rs = Wallets.getWallet(conn, id);
            APIUtils.sendResponse(httpExchange, 201, Wallets.serialize(rs, false));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
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

        String id = params.get("id");
        if (id != null) {
            String uid = params.get("uid");
            if (uid == null) {
                APIUtils.sendResponse(httpExchange, 400, format("field 'uid' is required"));
                return;
            }

            try {
                PublicKey key = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
                JsonWebToken jwt = new JsonWebToken(token, key);

                ArrayList<Integer> members = Wallets.getWalletMembers(conn, Integer.parseInt(id));
                if (!jwt.isAccessToken() || !members.contains(jwt.getUid())) {
                    APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                    return;
                }

                ResultSet rs = Wallets.getWallet(conn, Integer.parseInt(id));
                if (!rs.isBeforeFirst()) {
                    APIUtils.sendResponse(httpExchange, 404, format("not found"));
                    return;
                } else {
                    APIUtils.sendResponse(httpExchange, 200, Wallets.serialize(rs, false));
                    return;
                }
            } catch (InvalidJsonWebTokenException e) {
                APIUtils.sendResponse(httpExchange, 401, format(e.getMessage()));
                return;
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            } catch (Exception e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        String account = params.get("account");
        if (account != null) {
            try {
                PublicKey key = Accounts.getAccountPublicKey(conn, Integer.parseInt(account));
                JsonWebToken jwt = new JsonWebToken(token, key);

                if (!jwt.isAccessToken() || jwt.getUid() != Integer.parseInt(account)) {
                    APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                    return;
                }

                ResultSet rs = Wallets.getWalletsOfAccount(conn, Integer.parseInt(account));
                if (!rs.isBeforeFirst()) {
                    APIUtils.sendResponse(httpExchange, 404, format("not found"));
                    return;
                } else {
                    APIUtils.sendResponse(httpExchange, 200, Wallets.serialize(rs, true));
                    return;
                }
            } catch (InvalidJsonWebTokenException e) {
                APIUtils.sendResponse(httpExchange, 401, format(e.getMessage()));
                return;
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
                return;
            } catch (Exception e) {
                APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
                return;
            }
        }

        APIUtils.sendResponse(httpExchange, 400, format("field 'id' or 'account' is required"));
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

        String name = params.get("name");
        if (name == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'name' is required"));
            return;
        }

        try {
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            int owner = Wallets.getWalletOwner(conn, Integer.parseInt(id));
            if (!jwt.isAccessToken() || jwt.getUid() != owner) {
                APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                return;
            }
        } catch (InvalidJsonWebTokenException e) {
            APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
            return;
        } catch(SQLException e) {
            APIUtils.sendResponse(httpExchange, 404, format("not found"));
            return;
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
            return;
        }

        try {
            Wallets.updateWallet(conn, name, Integer.parseInt(id));
            ResultSet rs = Wallets.getWallet(conn, Integer.parseInt(id));
            APIUtils.sendResponse(httpExchange, 200, Wallets.serialize(rs, false));
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

            int owner = Wallets.getWalletOwner(conn, Integer.parseInt(id));
            if (!jwt.isAccessToken() || jwt.getUid() != owner) {
                APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                return;
            }
        } catch (InvalidJsonWebTokenException e) {
            APIUtils.sendResponse(httpExchange, 403, format(e.getMessage()));
            return;
        } catch(SQLException e) {
            APIUtils.sendResponse(httpExchange, 404, format("not found"));
            return;
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
            return;
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
