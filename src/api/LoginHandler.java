package api;

import auth.AuthHelper;
import com.sun.net.httpserver.HttpExchange;
import util.APIUtils;

import java.io.IOException;
import java.sql.Connection;
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
            // TODO(jp): ir buscar o salt e a hash a DB e validar; se for valido devolver 200, senao 401
            //AuthHelper.validatePassword(password, )
        } catch (Exception e) {
            // TODO(jp): handle das excecoes certas
        }
    }
}
