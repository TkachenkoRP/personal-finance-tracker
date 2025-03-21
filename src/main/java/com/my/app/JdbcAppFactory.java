package com.my.app;

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
import com.my.service.BudgetService;
import com.my.service.GoalService;
import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;
import com.my.service.impl.BudgetServiceImpl;
import com.my.service.impl.ConsoleNotificationServiceImpl;
import com.my.service.impl.EmailNotificationServiceImpl;
import com.my.service.impl.GoalServiceImpl;
import com.my.service.impl.TransactionCategoryServiceImpl;
import com.my.service.impl.TransactionServiceImpl;
import com.my.service.impl.UserServiceImpl;

import java.sql.SQLException;

public class JdbcAppFactory {

    public NotificationService createNotificationService() {
        return new ConsoleNotificationServiceImpl();
    }

    public NotificationService createEmailNotificationService() {
        return new EmailNotificationServiceImpl();
    }

    private TransactionCategoryRepository createTransactionCategoryRepository() throws SQLException {
        return new JdbcTransactionCategoryRepository();
    }

    public TransactionCategoryService createTransactionCategoryService() throws SQLException {
        return new TransactionCategoryServiceImpl(createTransactionCategoryRepository());
    }

    private BudgetRepository createBudgetRepository() throws SQLException {
        return new JdbcBudgetRepositoryImpl(createTransactionCategoryRepository());
    }

    public BudgetService createBudgetService() throws SQLException {
        return new BudgetServiceImpl(createBudgetRepository(), createTransactionRepository());
    }

    private GoalRepository createGoalRepository() throws SQLException {
        return new JdbcGoalRepositoryImpl(createTransactionCategoryRepository());
    }

    public GoalService createGoalService() throws SQLException {
        return new GoalServiceImpl(createGoalRepository(), createTransactionRepository(), createNotificationService(), createEmailNotificationService());
    }

    private UserRepository createUserRepository() throws SQLException {
        return new JdbcUserRepository(createBudgetRepository(), createGoalRepository());
    }

    public UserService createUserService() throws SQLException {
        return new UserServiceImpl(createUserRepository());
    }

    private TransactionRepository createTransactionRepository() throws SQLException {
        return new JdbcTransactionRepository(createTransactionCategoryRepository(), createUserRepository());
    }

    public TransactionService createTransactionService() throws SQLException {
        return new TransactionServiceImpl(createTransactionRepository(), createBudgetService(), createGoalService());
    }

    public ConsoleApp createConsoleApp() throws SQLException {
        return new ConsoleApp(
                createUserService(),
                createTransactionService(),
                createTransactionCategoryService()
        );
    }
}
