package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Accounts {
    public static void createAccount(Connection conn) throws SQLException {
        String query = "INSERT INTO Account DEFAULT VALUES";

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);

        stmt.close();
    }

    private static void getAccount() {}

    private static void updateAccount() {}

    private static void deleteAccount() {}
}
