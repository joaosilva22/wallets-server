package views;

import auth.AuthHelper;
import auth.InvalidJsonWebTokenException;
import auth.JsonWebToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import crypto.RSAKeyGenKt;
import database.Accounts;
import database.Wallets;
import util.APIUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

        try {
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            if (!jwt.isAccessToken()) {
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

        String id = params.get("id");
        if (id != null) {
            try {
                ResultSet rs = Accounts.getAccount(conn, Integer.parseInt(id));
                APIUtils.sendResponse(httpExchange, 200, Accounts.serialize(rs, false));
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
                return;
            }
        }

        String wallet = params.get("wallet");
        if (wallet != null) {
            try {
                ResultSet rs = Accounts.getAccountsFromWallet(conn, Integer.parseInt(wallet));
                APIUtils.sendResponse(httpExchange, 200, Accounts.serialize(rs, true));
            } catch (SQLException e) {
                APIUtils.sendResponse(httpExchange, 404, format("not found"));
                return;
            }
        }

        APIUtils.sendResponse(httpExchange, 400, format("field 'id' or 'wallet' is required"));
    }
}
