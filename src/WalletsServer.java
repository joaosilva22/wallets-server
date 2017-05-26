import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.sun.org.apache.xml.internal.security.Init;
import views.*;
import com.sun.net.httpserver.HttpServer;
import database.SQLiteConnection;
import util.IOUtils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.SQLException;

public class WalletsServer {
    public static void main(String[] args) {
        HttpServer server;
        Connection conn;

        Init.init();

        try {
            conn = SQLiteConnection.connect("database/wallets.db");
            conn.createStatement().execute("PRAGMA foreign_keys = ON");

            server = HttpServer.create(new InetSocketAddress(8000), 0)

            /*** NEW FROM HERE ***/ /*
            InetSocketAddress address = new InetSocketAddress(8000);

            server = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("testkey.jks");
            ks.load(fis, password);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        SSLContext c = SSLContext.getDefault();
                        SSLEngine engine = c.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                        params.setSSLParameters(defaultSSLParameters);

                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                    }
                }
            });
            */ /*** UNTIL HERE ***/

        } catch (IOException e) {
            IOUtils.err(e.getMessage());
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            IOUtils.err(e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            IOUtils.err(e.getMessage());
            e.printStackTrace();
            return;
        }

        server.createContext("/accounts", new AccountsHandler(conn));
        server.createContext("/wallets", new WalletsView(conn));
        server.createContext("/categories", new CategoriesView(conn));
        server.createContext("/movements", new MovementsView(conn));
        server.createContext("/auth", new LoginView(conn));
        server.createContext("/refresh", new RefreshTokenView(conn));
        server.createContext("/account-wallets", new AccountWalletsView(conn));

        server.start();

        IOUtils.log("Server listening on port 8000...");
    }
}
