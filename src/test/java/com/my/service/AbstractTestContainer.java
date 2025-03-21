package com.my.service;

import com.my.configuration.AppConfiguration;
import com.my.repository.BudgetRepository;
import com.my.repository.GoalRepository;
import com.my.repository.TransactionCategoryRepository;
import com.my.repository.TransactionRepository;
import com.my.repository.UserRepository;
import com.my.repository.impl.JdbcBudgetRepositoryImpl;
import com.my.repository.impl.JdbcGoalRepositoryImpl;
import com.my.repository.impl.JdbcTransactionCategoryRepository;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.repository.impl.JdbcUserRepository;
import com.my.service.impl.BudgetServiceImpl;
import com.my.service.impl.GoalServiceImpl;
import com.my.service.impl.TransactionCategoryServiceImpl;
import com.my.service.impl.TransactionServiceImpl;
import com.my.service.impl.UserServiceImpl;
import com.my.util.DBUtil;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
public abstract class AbstractTestContainer {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:17.4")
                    .waitingFor(Wait.forListeningPort());

    public static Connection testConnection;

    public static UserRepository userRepository;
    public static UserService userService;
    public static TransactionCategoryRepository transactionCategoryRepository;
    public static TransactionCategoryService transactionCategoryService;
    public static TransactionRepository transactionRepository;
    public static TransactionService transactionService;
    public static BudgetRepository budgetRepository;
    public static BudgetService budgetService;
    public static GoalRepository goalRepository;
    public static GoalService goalService;

    @BeforeAll
    static void setUp() throws SQLException, LiquibaseException {
        postgresContainer.start();
        String jdbcUrl = postgresContainer.getJdbcUrl();
        String username = postgresContainer.getUsername();
        String password = postgresContainer.getPassword();
        testConnection = DBUtil.getConnection(jdbcUrl, username, password, "tracker");

        try (Statement statement = testConnection.createStatement()) {
            statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS tracker;");
        }

        Contexts contexts = new Contexts("test");
        String changelogFile = AppConfiguration.getProperty("liquibase.change-log");
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(testConnection));
        Liquibase liquibase = new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);
        liquibase.update(contexts);

        transactionCategoryRepository = new JdbcTransactionCategoryRepository(testConnection);
        transactionCategoryService = new TransactionCategoryServiceImpl(transactionCategoryRepository);
        budgetRepository = new JdbcBudgetRepositoryImpl(transactionCategoryRepository);
        budgetService = new BudgetServiceImpl(budgetRepository, transactionRepository);
        goalRepository = new JdbcGoalRepositoryImpl(transactionCategoryRepository);
        goalService = new GoalServiceImpl(goalRepository, transactionRepository, null, null);
        userRepository = new JdbcUserRepository(testConnection, budgetRepository, goalRepository);
        userService = new UserServiceImpl(userRepository);

        transactionRepository = new JdbcTransactionRepository(testConnection, transactionCategoryRepository, userRepository);
        transactionService = new TransactionServiceImpl(transactionRepository, null, null);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        testConnection.close();
        postgresContainer.stop();
    }
}
