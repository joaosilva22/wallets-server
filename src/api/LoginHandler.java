package api;

import auth.AuthHelper;
import com.sun.net.httpserver.HttpExchange;
import database.Accounts;
import util.APIUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class LoginHandler extends BaseHandler {
    public LoginHandler(Connection conn) {
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
            if (AuthHelper.validatePassword(password, salt, hash)) {
                // TODO: gerar o access token e o refresh token e devolver no corpo da mensagem com o id
                APIUtils.sendResponse(httpExchange, 200, format("success"));
            } else {
                APIUtils.sendResponse(httpExchange, 401, format("invalid credentials"));
            }
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        } catch (NoSuchAlgorithmException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        } catch (InvalidKeySpecException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
