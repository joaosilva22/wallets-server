package database;

import util.IOUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {
    public static Connection connect(String path) throws SQLException {
        String url = "jdbc:sqlite:" + path;
        Connection conn = DriverManager.getConnection(url);
        IOUtils.log("Connection to SQLite has been established");
        return conn;
    }
}
