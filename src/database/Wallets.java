package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Wallets {
    public static int createWallet(Connection conn, String name, int owner) throws SQLException {
        String query = "INSERT INTO Wallet (name, owner) VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setInt(2, owner);
        stmt.executeUpdate();

        stmt.close();
        return stmt.getGeneratedKeys().getInt(1);
    }

    public static ResultSet getWallet(Connection conn, int id) throws SQLException {
        String query = "SELECT * FROM Wallet WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);

        return stmt.executeQuery();
    }

    public static void updateWallet(Connection conn, String name, int id) throws SQLException {
        String query = "UPDATE Wallet SET name = ? WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
    }

    public static int deleteWallet(Connection conn, int id) throws SQLException {
        String query = "DELETE FROM Wallet WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
        int n = stmt.executeUpdate();

        stmt.close();
        return n;
    }

    public static ResultSet getWalletsOfAccount(Connection conn, int account) throws SQLException {
        // String query = "SELECT Wallet.* FROM Account INNER JOIN Wallet ON Account.id = Wallet.owner WHERE id = ?"
    }

    public static String serialize(ResultSet data) throws SQLException {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", data.getObject("name").toString());
        fields.put("owner", data.getObject("owner").toString());

        Gson gson = new GsonBuilder().create();
        return gson.toJson(fields);
    }
}
