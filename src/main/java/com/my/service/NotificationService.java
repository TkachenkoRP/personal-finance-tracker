package com.my.service;

/**
 * Интерфейс для управления уведомлениями.
 */
public interface NotificationService {
    /**
     * Отправка уведомления с заданным сообщением.
     *
     * @param msg сообщение, которое будет отправлено в уведомлении
     */
    void sendNotification(String msg);
}
