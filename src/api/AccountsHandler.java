package api;

import auth.AuthHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.Accounts;
import util.APIUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class AccountsHandler extends BaseHandler {
    public AccountsHandler(Connection conn) {
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

        String first_name = params.get("first_name");
        if (first_name == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'first_name' is required"));
            return;
        }

        String last_name = params.get("last_name");
        if (last_name == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'last_name' is required"));
            return;
        }

        try {
            String salt = AuthHelper.getSalt();
            String hash = AuthHelper.generatePasswordHash(password, salt);

            int id = Accounts.createAccount(conn, email, salt, hash, first_name, last_name);
            ResultSet rs = Accounts.getAccount(conn, id);
            APIUtils.sendResponse(httpExchange, 201, Accounts.serialize(rs, false));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        } catch (NoSuchAlgorithmException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        } catch (InvalidKeySpecException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
