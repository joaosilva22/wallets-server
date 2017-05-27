package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AccountWallets {
    public static int createAccountWallet(Connection conn, int account, int wallet) throws SQLException {
        String query = "INSERT INTO AccountWallet (account, wallet) VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, account);
        stmt.setInt(2, wallet);
        stmt.executeUpdate();

        stmt.close();
        return stmt.getGeneratedKeys().getInt(1);
    }

    public static ResultSet getAccountWallet(Connection conn, int account, int wallet) throws SQLException {
        String query = "SELECT account, wallet FROM AccountWallet WHERE account = ? AND wallet = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, account);
        stmt.setInt(2, wallet);

        return stmt.executeQuery();
    }

    public static int deleteAccountWallet(Connection conn, int account, int wallet) throws SQLException {
        String query = "DELETE FROM AccountWallet WHERE account = ? AND wallet = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, account);
        stmt.setInt(2, wallet);
        int n = stmt.executeUpdate();

        stmt.close();
        return n;
    }

    public static int getAccountId(Connection conn, String email) throws SQLException {
        String query = "SELECT id FROM Account WHERE email = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);

        ResultSet rs = stmt.executeQuery();
        int id = -1;
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    public static String serialize(ResultSet data) throws SQLException {
        Map<String, Integer> fields = new HashMap<>();
        fields.put("account", data.getInt("account"));
        fields.put("wallet", data.getInt("wallet"));

        Gson gson = new GsonBuilder().create();
        return gson.toJson(fields);
    }
}
