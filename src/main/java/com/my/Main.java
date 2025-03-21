package com.my;

import com.my.app.ConsoleApp;
import com.my.app.JdbcAppFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;

public class Main {
    public static void main(String[] args) {
        final Logger logger = LogManager.getRootLogger();
        try {
            JdbcAppFactory appFactory = new JdbcAppFactory();
            ConsoleApp consoleApp = appFactory.createConsoleApp();
            consoleApp.start();
        } catch (SQLException e) {
            logger.log(Level.ERROR, MessageFormat.format("Ошибка соединения с БД. {0}", e.getMessage()));
        }
    }
}