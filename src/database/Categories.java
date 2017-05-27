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

public class Categories {
    public static int createCategory(Connection conn, String name, float amount, int wallet) throws SQLException {
        String query = "INSERT INTO Category (name, amount, wallet) VALUES (?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setFloat(2, amount);
        stmt.setInt(3, wallet);
        stmt.executeUpdate();

        stmt.close();
        return stmt.getGeneratedKeys().getInt(1);
    }

    public static ResultSet getCategory(Connection conn, int id) throws SQLException {
        String query = "SELECT * FROM Category WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);

        return stmt.executeQuery();
    }

    public static void updateCategoryName(Connection conn, int id, String name) throws SQLException {
        String query = "UPDATE Category SET name = ? WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
    }

    public static void updateCategoryAmount(Connection conn, int id, float amount) throws SQLException {
        String query = "UPDATE Category SET amount = ? WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setFloat(1, amount);
        stmt.setInt(2, id);
        stmt.executeUpdate();

        stmt.close();
    }

    public static int deleteCategory(Connection conn, int id) throws SQLException {
        String query = "DELETE FROM Category WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
        int n = stmt.executeUpdate();

        stmt.close();
        return n;
    }

    public static ResultSet getCategoriesOfWallet(Connection conn, int wallet) throws SQLException {
        String query = "SELECT Category.* FROM Category INNER JOIN Wallet ON Category.wallet = Wallet.id WHERE Wallet.id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, wallet);

        return stmt.executeQuery();
    }

    public static int getCategoryWallet(Connection conn, int category) throws SQLException {
        String query = "SELECT wallet FROM Category WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, category);

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
            list.put("categories", new ArrayList<>());

            while (data.next()) {
                Map<String, String> fields = new HashMap<>();
                fields.put("id", data.getObject("id").toString());
                fields.put("name", data.getObject("name").toString());
                fields.put("amount", data.getObject("amount").toString());
                fields.put("wallet", data.getObject("wallet").toString());
                list.get("categories").add(fields);
            }

            Gson gson = new GsonBuilder().create();
            return gson.toJson(list);
        }

        Map<String, String> fields = new HashMap<>();
        fields.put("id", data.getObject("id").toString());
        fields.put("name", data.getObject("name").toString());
        fields.put("amount", data.getObject("amount").toString());
        fields.put("wallet", data.getObject("wallet").toString());

        Gson gson = new GsonBuilder().create();
        return gson.toJson(fields);
    }
}
