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
    public static void createWallet(Connection conn, String name, int owner) throws SQLException {
        String query = "INSERT INTO Wallet (name, owner) VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setInt(2, owner);
        stmt.executeUpdate();

        stmt.close();
    }

    public static ResultSet getWallet(Connection conn, int id) throws SQLException {
        String query = "SELECT * FROM Wallet WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        return rs;
    }

    public static void updateWallet() {}

    public static void deleteWallet() {}

    public static String serialize(ResultSet data) throws SQLException {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", data.getObject("name").toString());
        fields.put("owner", data.getObject("owner").toString());

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(fields);
        return json;
    }
}
