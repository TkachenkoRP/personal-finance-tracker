package com.my;

import com.my.app.AppFactory;
import com.my.app.ConsoleApp;
import com.my.app.JdbcAppFactory;
import com.my.configuration.DatabaseConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConfiguration.getConnection()) {
            AppFactory appFactory = new JdbcAppFactory(connection);
            ConsoleApp consoleApp = appFactory.createConsoleApp();
            consoleApp.start();
        } catch (SQLException e) {
            System.err.println("Ошибка соединения с БД. " + e.getMessage());
        }
    }
}