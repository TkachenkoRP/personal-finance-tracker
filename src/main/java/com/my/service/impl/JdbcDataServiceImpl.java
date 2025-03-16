package com.my.service.impl;

import com.my.configuration.AppConfiguration;
import com.my.configuration.DatabaseConfiguration;
import com.my.service.JdbcDataService;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcDataServiceImpl implements JdbcDataService {

    private Connection connection;

    public JdbcDataServiceImpl() {
        try {
            connection = DatabaseConfiguration.getConnection(AppConfiguration.getProperty("liquibase.schema"));
        } catch (SQLException e) {
            System.err.println("Failed to create connection database: " + e.getMessage());
        }
    }

    @Override
    public void initDb() throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(AppConfiguration.getProperty("liquibase.change-log"), new ClassLoaderResourceAccessor(), database);
        liquibase.update("dev");
    }
}
