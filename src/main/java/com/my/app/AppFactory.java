package com.my.app;

import com.my.service.JdbcDataService;
import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;

public interface AppFactory {
    UserService createUserService();
    TransactionService createTransactionService();
    TransactionCategoryService createTransactionCategoryService();
    NotificationService createNotificationService();
    NotificationService createEmailNotificationService();
    JdbcDataService createJdbcDataService();
    ConsoleApp createConsoleApp();
}
