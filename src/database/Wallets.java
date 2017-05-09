package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Wallets {
    public static void createWallet(Connection conn, String name, int owner) throws SQLException {
        String query = "INSERT INTO Wallet (name, owner) VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setInt(2, owner);
        stmt.executeUpdate();

        stmt.close();
    }

    public static void getWallet() {}

    public static void updateWallet() {}

    public static void deleteWallet() {}
}
