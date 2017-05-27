package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallets {
    public static int createWallet(Connection conn, String name, int owner) throws SQLException {
        String query = "INSERT INTO Wallet (name, owner) VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setInt(2, owner);
        stmt.executeUpdate();

        int wallet = stmt.getGeneratedKeys().getInt(1);
        stmt.close();

        query = "INSERT INTO AccountWallet(account, wallet) VALUES (?, ?)";

        stmt = conn.prepareStatement(query);
        stmt.setInt(1, owner);
        stmt.setInt(2, wallet);
        stmt.executeUpdate();

        stmt.close();
        return wallet;
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
        String query = "SELECT Wallet.* FROM Wallet INNER JOIN AccountWallet ON Wallet.id = AccountWallet.wallet WHERE AccountWallet.account = ? GROUP BY Wallet.id";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, account);

        return stmt.executeQuery();
    }

    public static ArrayList<Integer> getWalletMembers(Connection conn, int wallet) throws SQLException {
        String query = "SELECT account FROM AccountWallet WHERE wallet = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, wallet);

        ArrayList<Integer> result = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            result.add(rs.getInt("account"));
        }
        return result;
    }

    public static int getWalletOwner(Connection conn, int wallet) throws SQLException {
        String query = "SELECT owner FROM Wallet WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, wallet);

        ResultSet rs = stmt.executeQuery();
        int owner = -1;
        if (rs.next()) {
            owner = rs.getInt("owner");
        }
        return owner;
    }

    public static String serialize(ResultSet data, boolean many) throws SQLException {
        if (many) {
            Map<String, ArrayList<Map<String, String>>> list = new HashMap<>();
            list.put("wallets", new ArrayList<>());

            while (data.next()) {
                Map<String, String> fields = new HashMap<>();
                fields.put("id", data.getObject("id").toString());
                fields.put("name", data.getObject("name").toString());
                fields.put("owner", data.getObject("owner").toString());
                list.get("wallets").add(fields);
            }

            Gson gson = new GsonBuilder().create();
            return gson.toJson(list);
        }

        Map<String, String> fields = new HashMap<>();
        fields.put("id", data.getObject("id").toString());
        fields.put("name", data.getObject("name").toString());
        fields.put("owner", data.getObject("owner").toString());

        Gson gson = new GsonBuilder().create();
        return gson.toJson(fields);
    }
}
