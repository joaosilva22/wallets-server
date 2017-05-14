import api.*;
import auth.AuthHelper;
import com.sun.net.httpserver.HttpServer;
import database.SQLiteConnection;
import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

public class WalletsServer {
    public static void main(String[] args) {
        HttpServer server;
        Connection conn;

        try {
            conn = SQLiteConnection.connect("database/wallets.db");
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            IOUtils.err(e.getMessage());
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            IOUtils.err(e.getMessage());
            e.printStackTrace();
            return;
        }

        server.createContext("/accounts", new AccountsHandler(conn));
        server.createContext("/wallets", new WalletsHandler(conn));
        server.createContext("/categories", new CategoriesHandler(conn));
        server.createContext("/movements", new MovementsHandler(conn));
        server.start();

        IOUtils.log("Server listening on port 8000...");
    }
}
