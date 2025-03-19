package com.my.app;

import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;

import java.sql.SQLException;

public interface AppFactory {
    UserService createUserService() throws SQLException;
    TransactionService createTransactionService() throws SQLException;
    TransactionCategoryService createTransactionCategoryService() throws SQLException;
    NotificationService createNotificationService();
    NotificationService createEmailNotificationService();
    ConsoleApp createConsoleApp() throws SQLException;
}
