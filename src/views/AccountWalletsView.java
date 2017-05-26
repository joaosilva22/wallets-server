package views;

import auth.InvalidJsonWebTokenException;
import auth.JsonWebToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import database.AccountWallets;
import database.Accounts;
import database.Wallets;
import util.APIUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class AccountWalletsView extends BaseView {
    public AccountWalletsView(Connection conn) {
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

        String account = params.get("account");
        if (account == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'account' is required"));
            return;
        }

        String wallet = params.get("wallet");
        if (wallet == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'wallet' is required"));
            return;
        }

        try {
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            int owner = Wallets.getWalletOwner(conn, Integer.parseInt(wallet));
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
            AccountWallets.createAccountWallet(conn, Integer.parseInt(account), Integer.parseInt(wallet));
            ResultSet rs = AccountWallets.getAccountWallet(conn, Integer.parseInt(account), Integer.parseInt(wallet));
            APIUtils.sendResponse(httpExchange, 200, AccountWallets.serialize(rs));
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

        String account = params.get("account");
        if (account == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'account' is required"));
            return;
        }

        String wallet = params.get("wallet");
        if (wallet == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'wallet' is required"));
            return;
        }

        try {
            PublicKey pub = Accounts.getAccountPublicKey(conn, Integer.parseInt(uid));
            JsonWebToken jwt = new JsonWebToken(token, pub);

            int owner = Wallets.getWalletOwner(conn, Integer.parseInt(wallet));
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
            AccountWallets.deleteAccountWallet(conn, Integer.parseInt(account), Integer.parseInt(wallet));
            APIUtils.sendResponse(httpExchange, 200, format("deleted account-wallet"));
        } catch (SQLException e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
