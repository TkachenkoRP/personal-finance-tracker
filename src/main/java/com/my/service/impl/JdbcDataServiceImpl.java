package com.my.service.impl;

import com.my.configuration.AppConfiguration;
import com.my.service.JdbcDataService;
import com.my.util.DBUtil;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

public class JdbcDataServiceImpl implements JdbcDataService {
    private static final Logger logger = LogManager.getRootLogger();

    private Connection connection;

    public JdbcDataServiceImpl() {
        try {
            connection = DBUtil.getConnection(AppConfiguration.getProperty("liquibase.schema"));
        } catch (SQLException e) {
            logger.log(Level.ERROR, MessageFormat.format("Failed to create connection database: {0}", e.getMessage()));
        }
    }

    @Override
    public void initDb() throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(AppConfiguration.getProperty("liquibase.change-log"), new ClassLoaderResourceAccessor(), database);
        liquibase.update("dev");
    }
}
