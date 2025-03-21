package com.my.app;

import com.my.repository.TransactionCategoryRepository;
import com.my.repository.TransactionRepository;
import com.my.repository.UserRepository;
import com.my.repository.impl.InMemoryTransactionCategoryRepository;
import com.my.repository.impl.InMemoryTransactionRepository;
import com.my.repository.impl.InMemoryUserRepository;
import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;
import com.my.service.impl.ConsoleNotificationServiceImpl;
import com.my.service.impl.EmailNotificationServiceImpl;
import com.my.service.impl.TransactionCategoryServiceImpl;
import com.my.service.impl.TransactionServiceImpl;
import com.my.service.impl.UserServiceImpl;

public class InMemoryAppFactory implements AppFactory {
    @Override
    public UserService createUserService() {
        UserRepository userRepository = new InMemoryUserRepository();
        return new UserServiceImpl(userRepository);
    }

    @Override
    public TransactionService createTransactionService() {
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        return new TransactionServiceImpl(transactionRepository, createUserService(), createNotificationService(), createEmailNotificationService());
    }

    @Override
    public TransactionCategoryService createTransactionCategoryService() {
        TransactionCategoryRepository transactionCategoryRepository = new InMemoryTransactionCategoryRepository();
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
                createTransactionCategoryService()
        );
    }
}
