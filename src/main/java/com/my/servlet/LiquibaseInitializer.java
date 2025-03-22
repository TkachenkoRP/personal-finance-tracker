package com.my.servlet;

import com.my.service.JdbcDataService;
import com.my.service.impl.JdbcDataServiceImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.exception.LiquibaseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebListener
public class LiquibaseInitializer implements ServletContextListener {
    private static final Logger logger = LogManager.getRootLogger();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            JdbcDataService jdbcDataService = new JdbcDataServiceImpl();
            jdbcDataService.initDb();
        } catch (LiquibaseException e) {
            logger.log(Level.ERROR, e);
        }
    }
}
