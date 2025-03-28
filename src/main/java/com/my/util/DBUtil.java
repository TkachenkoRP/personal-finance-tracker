package com.my.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DBUtil {
    @Value("database.url")
    private String url;
    @Value("database.username")
    private String username;
    @Value("database.password")
    private String password;
    @Value("datasource.schema")
    private String schema;

    public Connection getConnection() {
        return getConnection(schema);
    }

    public Connection getConnection(String schema) {
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
