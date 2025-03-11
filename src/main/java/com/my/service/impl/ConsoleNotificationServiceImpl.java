package com.my.service.impl;

import com.my.out.ConsoleOutputHandler;
import com.my.service.NotificationService;

public class ConsoleNotificationServiceImpl implements NotificationService {
    @Override
    public void sendNotification(String msg) {
        ConsoleOutputHandler.displayMsg("НАПОМИНАНИЕ: " + msg);
    }
}
