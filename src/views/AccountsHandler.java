package views;

import auth.AuthHelper;
import com.sun.net.httpserver.HttpExchange;
import crypto.RSAKeyGenKt;
import database.Accounts;
import util.APIUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class AccountsHandler extends BaseView {
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

        String firstName = params.get("first_name");
        if (firstName == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'first_name' is required"));
            return;
        }

        String lastName = params.get("last_name");
        if (lastName == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'last_name' is required"));
            return;
        }

        try {
            String salt = AuthHelper.getSalt();
            String hash = AuthHelper.generatePasswordHash(password, salt);

            KeyPair pair = RSAKeyGenKt.generateKeyPair();
            String privateKey = RSAKeyGenKt.toString(pair.getPrivate());
            String publicKey = RSAKeyGenKt.toString(pair.getPublic());

            int id = Accounts.createAccount(conn, email, salt, hash, firstName, lastName, privateKey, publicKey);
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
