package com.my;

import com.my.app.ConsoleApp;
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

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        UserService userService = new UserServiceImpl(userRepository);
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository);
        TransactionCategoryRepository transactionCategoryRepository = new InMemoryTransactionCategoryRepository();
        TransactionCategoryService transactionCategoryService = new TransactionCategoryServiceImpl(transactionCategoryRepository);
        NotificationService notificationService = new ConsoleNotificationServiceImpl();
        NotificationService emailNotificationService = new EmailNotificationServiceImpl();
        ConsoleApp consoleApp = new ConsoleApp(userService, transactionService, transactionCategoryService, notificationService, emailNotificationService);
        consoleApp.start();
    }
}