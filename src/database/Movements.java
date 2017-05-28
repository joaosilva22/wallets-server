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

public class Movements {
    public static int createMovement(Connection conn, String name, String description, float amount, int category) throws SQLException {
        String query = "INSERT INTO Movement (name, description, amount, category) VALUES (?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setString(2, description);
        stmt.setFloat(3, amount);
        stmt.setInt(4, category);
        stmt.executeUpdate();

        stmt.close();
        return stmt.getGeneratedKeys().getInt(1);
    }

    public static ResultSet getMovement(Connection conn, int id) throws SQLException {
        String query = "SELECT * FROM Movement WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);

        return stmt.executeQuery();
    }

    public static void updateMovementName(Connection conn, int id, String name) throws SQLException {
        String query = "UPDATE Movement SET name = ? WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
    }

    public static void updateMovementDescription(Connection conn, int id, String description) throws SQLException {
        String query = "UPDATE Movement SET description = ? WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, description);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
    }

    public static void updateMovementAmount(Connection conn, int id, float amount) throws SQLException {
        String query = "UPDATE Movement SET amount = ? WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setFloat(1, amount);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
    }

    public static void updateMovementCategory(Connection conn, int id, int category) throws SQLException {
        String query = "UPDATE Movement SET category = ? WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, category);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
    }

    public static int deleteMovement(Connection conn, int id) throws SQLException {
        String query = "DELETE FROM Movement WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
        int n = stmt.executeUpdate();

        stmt.close();
        return n;
    }

    public static ResultSet getMovementsOfWallet(Connection conn, int wallet) throws SQLException {
        String query = "SELECT Movement.* FROM Movement INNER JOIN Category ON Movement.category = Category.id WHERE Category.wallet = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, wallet);

        return stmt.executeQuery();
    }

    public static int getMovementWallet(Connection conn, int movement) throws SQLException {
        String query = "SELECT wallet FROM Category INNER JOIN Movement ON Movement.category = Category.id WHERE Movement.id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, movement);

        int wallet = -1;
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            wallet = rs.getInt("wallet");
        }
        return wallet;
    }

    public static String serialize(ResultSet data, boolean many) throws SQLException {
        if (many) {
            Map<String, ArrayList<Map<String, String>>> list = new HashMap<>();
            list.put("movements", new ArrayList<>());

            while (data.next()) {
                Map<String, String> fields = new HashMap<>();
                fields.put("id", data.getObject("id").toString());
                fields.put("name", data.getObject("name").toString());
                fields.put("description", data.getObject("description").toString());
                fields.put("amount", data.getObject("amount").toString());
                fields.put("category", data.getObject("category").toString());
                list.get("movements").add(fields);
            }

            Gson gson = new GsonBuilder().create();
            return gson.toJson(list);
        }

        Map<String, String> fields = new HashMap<>();
        fields.put("id", data.getObject("id").toString());
        fields.put("name", data.getObject("name").toString());
        fields.put("description", data.getObject("description").toString());
        fields.put("amount", data.getObject("amount").toString());
        fields.put("category", data.getObject("category").toString());

        Gson gson = new GsonBuilder().create();
        return gson.toJson(fields);
    }
}
