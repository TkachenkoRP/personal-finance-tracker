package com.my.service;

import liquibase.exception.LiquibaseException;

public interface JdbcDataService {
    void initDb() throws LiquibaseException;
}
