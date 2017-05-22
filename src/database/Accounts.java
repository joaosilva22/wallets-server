package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.RSAKeyGenKt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Accounts {
    public static int createAccount(Connection conn, String email, String salt, String password, String firstName, String lastName, String privateKey, String publicKey) throws SQLException {
        String query = "INSERT INTO Account (email, salt, password, first_name, last_name, private_key, public_key) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, salt);
        stmt.setString(3, password);
        stmt.setString(4, firstName);
        stmt.setString(5, lastName);
        stmt.setString(6, privateKey);
        stmt.setString(7, publicKey);
        stmt.executeUpdate();

        stmt.close();
        return stmt.getGeneratedKeys().getInt(1);
    }

    public static ResultSet getAccount(Connection conn, int id) throws SQLException {
        String query = "SELECT * FROM Account WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);

        return stmt.executeQuery();
    }

    private static void updateAccount() {}

    private static void deleteAccount() {}

    public static int getAccountId(Connection conn, String email) throws SQLException {
        String query = "SELECT id FROM Account WHERE email = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);

        ResultSet rs = stmt.executeQuery();
        return rs.getInt("id");
    }

    public static String getAccountPassword(Connection conn, String email) throws SQLException {
        String query = "SELECT password FROM Account WHERE email = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);

        ResultSet rs = stmt.executeQuery();
        return rs.getString("password");
    }

    public static String getAccountSalt(Connection conn, String email) throws SQLException {
        String query = "SELECT salt FROM Account WHERE email = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);

        ResultSet rs = stmt.executeQuery();
        return rs.getString("salt");
    }

    public static PrivateKey getAccountPrivateKey(Connection conn, int id) throws SQLException {
        String query = "SELECT private_key FROM Account WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        String privateKey = rs.getString("private_key");

        return RSAKeyGenKt.toPrivateKey(privateKey);
    }

    public static PublicKey getAccountPublicKey(Connection conn, int id) throws SQLException {
        String query = "SELECT public_key FROM Account WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        String publicKey = rs.getString("public_key");

        return RSAKeyGenKt.toPublicKey(publicKey);
    }

    public static String serialize(ResultSet data, boolean many) throws SQLException {
        if (many) {
            Map<String, ArrayList<Map<String, String>>> list = new HashMap<>();
            list.put("accounts", new ArrayList<>());

            while (data.next()) {
                Map<String, String> fields = new HashMap<>();
                fields.put("id", data.getObject("id").toString());
                fields.put("email", data.getObject("email").toString());
                fields.put("first_name", data.getObject("first_name").toString());
                fields.put("last_name", data.getObject("last_name").toString());
                list.get("accounts").add(fields);
            }

            Gson gson = new GsonBuilder().create();
            return gson.toJson(list);
        }

        Map<String, String> fields = new HashMap<>();
        fields.put("id", data.getObject("id").toString());
        fields.put("email", data.getObject("email").toString());
        fields.put("first_name", data.getObject("first_name").toString());
        fields.put("last_name", data.getObject("last_name").toString());

        Gson gson = new GsonBuilder().create();
        return gson.toJson(fields);
    }
}
