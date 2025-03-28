package com.my.service.impl;

import com.my.service.JdbcDataService;
import com.my.util.DBUtil;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcDataServiceImpl implements JdbcDataService {
    private final DBUtil dbUtil;

    @Value("liquibase.change-log")
    private String liquibaseChangeLog;

    @Override
    public void initDb() throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dbUtil.getConnection()));
        Liquibase liquibase = new Liquibase(liquibaseChangeLog, new ClassLoaderResourceAccessor(), database);
        liquibase.update("dev");
    }
}
