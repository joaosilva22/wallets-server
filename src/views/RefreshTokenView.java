package views;

import auth.InvalidJsonWebTokenException;
import auth.JsonWebToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import database.Accounts;
import util.APIUtils;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.util.Map;

public class RefreshTokenView extends BaseView {
    public RefreshTokenView(Connection conn) {
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

        String id = params.get("id");
        if (id == null) {
            APIUtils.sendResponse(httpExchange, 400, format("field 'id' is required"));
            return;
        }

        try {
            int uid = Integer.parseInt(id);

            PublicKey pub = Accounts.getAccountPublicKey(conn, uid);
            JsonWebToken old = new JsonWebToken(token, pub);

            if (uid != old.getUid()) {
                APIUtils.sendResponse(httpExchange, 403, format("forbidden"));
                return;
            }

            PrivateKey prv = Accounts.getAccountPrivateKey(conn, uid);
            JsonWebToken access = new JsonWebToken(JsonWebToken.ACCESS_TOKEN, uid, prv);
            JsonWebToken refresh = new JsonWebToken(JsonWebToken.REFRESH_TOKEN, uid, prv);

            String tokens = "{\"access\":\"" + access.getToken() + "\",\"refresh\":\"" + refresh.getToken() + "\"}";
            APIUtils.sendResponse(httpExchange, 200, tokens);
        } catch (InvalidJsonWebTokenException e) {
            APIUtils.sendResponse(httpExchange, 401, format(e.getMessage()));
        } catch (Exception e) {
            APIUtils.sendResponse(httpExchange, 500, format(e.getMessage()));
        }
    }
}
