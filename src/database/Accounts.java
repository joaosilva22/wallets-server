package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Accounts {
    public static int createAccount(Connection conn, String email, String salt, String password, String first_name, String last_name) throws SQLException {
        String query = "INSERT INTO Account (email, salt, password, first_name, last_name) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, salt);
        stmt.setString(3, password);
        stmt.setString(4, first_name);
        stmt.setString(5, last_name);
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
