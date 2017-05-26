package views;

import auth.AuthHelper;
import auth.JsonWebToken;
import com.sun.net.httpserver.HttpExchange;
import database.Accounts;
import util.APIUtils;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class LoginView extends BaseView {
    public LoginView(Connection conn) {
        super(conn);
    }

    @Override
    protected void post(HttpExchange httpExchange) throws IOException {
        Map<String, String> params = APIUtils.getPOSTparams(httpExchange);

        String email = params.get("email");
        if (email == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'email' is required"));
            return;
        }

        String password = params.get("password");
        if (password == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'password' is required"));
            return;
        }

        try {
            String hash = Accounts.getAccountPassword(conn, email);
            String salt = Accounts.getAccountSalt(conn, email);
            int id = Accounts.getAccountId(conn, email);

            if (AuthHelper.validatePassword(password, salt, hash)) {
                PrivateKey prv = Accounts.getAccountPrivateKey(conn, id);
                PublicKey pub = Accounts.getAccountPublicKey(conn, id);

                JsonWebToken access = new JsonWebToken(JsonWebToken.ACCESS_TOKEN, id, prv);
                JsonWebToken refresh = new JsonWebToken(JsonWebToken.REFRESH_TOKEN, id, prv);

                String tokens = "{\"id\":" + id + ",\"access\":\"" + access.getToken() + "\",\"refresh\":\"" + refresh.getToken() + "\"}";
                APIUtils.sendResponse(httpExchange, 200, tokens);
            } else {
                APIUtils.sendResponse(httpExchange, 401, format("invalid credentials"));
            }
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
