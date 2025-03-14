package com.my.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfiguration {
    public static Connection getConnection() throws SQLException {
        return getConnection("trainings");
    }

    public static Connection getConnection(String schema) throws SQLException {
        String url = AppConfiguration.getProperty("database.url") + schema;
        String username = AppConfiguration.getProperty("database.username");
        String password = AppConfiguration.getProperty("database.password");
        return DriverManager.getConnection(url, username, password);
    }
}