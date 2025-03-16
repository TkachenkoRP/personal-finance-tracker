package com.my.app;

import com.my.repository.TransactionCategoryRepository;
import com.my.repository.TransactionRepository;
import com.my.repository.UserRepository;
import com.my.repository.impl.JdbcTransactionCategoryRepository;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.repository.impl.JdbcUserRepository;
import com.my.service.JdbcDataService;
import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;
import com.my.service.impl.ConsoleNotificationServiceImpl;
import com.my.service.impl.EmailNotificationServiceImpl;
import com.my.service.impl.JdbcDataServiceImpl;
import com.my.service.impl.TransactionCategoryServiceImpl;
import com.my.service.impl.TransactionServiceImpl;
import com.my.service.impl.UserServiceImpl;

import java.sql.Connection;

public class JdbcAppFactory implements AppFactory {
    private final Connection connection;

    public JdbcAppFactory(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserService createUserService() {
        UserRepository userRepository = new JdbcUserRepository(connection);
        return new UserServiceImpl(userRepository);
    }

    @Override
    public TransactionService createTransactionService() {
        TransactionCategoryRepository transactionCategoryRepository = new JdbcTransactionCategoryRepository(connection);
        UserRepository userRepository = new JdbcUserRepository(connection);
        TransactionRepository transactionRepository = new JdbcTransactionRepository(connection, transactionCategoryRepository, userRepository);
        return new TransactionServiceImpl(transactionRepository);
    }

    @Override
    public TransactionCategoryService createTransactionCategoryService() {
        TransactionCategoryRepository transactionCategoryRepository = new JdbcTransactionCategoryRepository(connection);
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
    public ConsoleApp createConsoleApp() {
        return new ConsoleApp(
                createUserService(),
                createTransactionService(),
                createTransactionCategoryService(),
                createNotificationService(),
                createEmailNotificationService()
        );
    }
}
