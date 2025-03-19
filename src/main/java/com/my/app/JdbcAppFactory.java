package com.my.app;

import com.my.repository.TransactionCategoryRepository;
import com.my.repository.UserRepository;
import com.my.repository.impl.JdbcTransactionCategoryRepository;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.repository.impl.JdbcUserRepository;
import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;
import com.my.service.impl.ConsoleNotificationServiceImpl;
import com.my.service.impl.EmailNotificationServiceImpl;
import com.my.service.impl.TransactionCategoryServiceImpl;
import com.my.service.impl.TransactionServiceImpl;
import com.my.service.impl.UserServiceImpl;

import java.sql.SQLException;

public class JdbcAppFactory implements AppFactory {

    @Override
    public UserService createUserService() throws SQLException {
        UserRepository userRepository = new JdbcUserRepository();
        return new UserServiceImpl(userRepository);
    }

    @Override
    public TransactionService createTransactionService() throws SQLException {
        TransactionCategoryRepository transactionCategoryRepository = new JdbcTransactionCategoryRepository();
        UserRepository userRepository = new JdbcUserRepository();
        JdbcTransactionRepository transactionRepository = new JdbcTransactionRepository(transactionCategoryRepository, userRepository);
        return new TransactionServiceImpl(transactionRepository);
    }

    @Override
    public TransactionCategoryService createTransactionCategoryService() throws SQLException {
        TransactionCategoryRepository transactionCategoryRepository = new JdbcTransactionCategoryRepository();
        return new TransactionCategoryServiceImpl(transactionCategoryRepository);
    }

    @Override
    public NotificationService createNotificationService() {
        return new ConsoleNotificationServiceImpl();
    }

    @Override
    public NotificationService createEmailNotificationService() {
        return new EmailNotificationServiceImpl();
    }

    @Override
    public ConsoleApp createConsoleApp() throws SQLException {
        return new ConsoleApp(
                createUserService(),
                createTransactionService(),
                createTransactionCategoryService(),
                createNotificationService(),
                createEmailNotificationService()
        );
    }
}
