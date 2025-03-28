package com.my.service.impl;

import com.my.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements NotificationService {
    private static final Logger logger = LogManager.getRootLogger();
    @Override
    public void sendNotification(String msg) {
        logger.log(Level.DEBUG, "Send email notification: {}", msg);
        logger.log(Level.INFO, "Send email notification: {}", msg);
    }
}
