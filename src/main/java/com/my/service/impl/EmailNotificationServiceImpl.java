package com.my.service.impl;

import com.my.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements NotificationService {
    @Override
    public void sendNotification(String msg) {
        log.debug("Send email notification: {}", msg);
        log.info("Send email notification: {}", msg);
    }
}
