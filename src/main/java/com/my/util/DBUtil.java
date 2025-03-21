package com.my.util;

import com.my.configuration.AppConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private DBUtil() {
    }

    public static Connection getConnection() {
        return getConnection(AppConfiguration.getProperty("database.schema"));
    }

    public static Connection getConnection(String schema) {
        String url = AppConfiguration.getProperty("database.url");
        String username = AppConfiguration.getProperty("database.username");
        String password = AppConfiguration.getProperty("database.password");
        return getConnection(url, username, password, schema);
    }

    public static Connection getConnection(String url, String username, String password, String schema) {
        Connection connection;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
            connection.setSchema(schema);
        } catch (SQLException | ClassNotFoundException e) {
            return null;
        }
        return connection;
    }
}
