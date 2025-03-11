package com.my.service;

import com.my.model.User;

import java.util.List;

/**
 * Интерфейс для управления пользователями в системе.
 */
public interface UserService {
    /**
     * Регистрация нового пользователя.
     *
     * @param email адрес электронной почты пользователя
     * @param name имя пользователя
     * @param password пароль пользователя
     * @return зарегистрированный пользователь
     */
    User registration(String email, String name, String password);

    /**
     * Вход пользователя в систему.
     *
     * @param email адрес электронной почты пользователя
     * @param password пароль пользователя
     * @return пользователь, который вошел в систему
     */
    User login(String email, String password);

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return пользователь с указанным идентификатором
     */
    User getById(Long id);

    /**
     * Обновление информации о пользователе.
     *
     * @param id идентификатор пользователя
     * @param sourceUser объект пользователя с обновленной информацией
     * @return обновленный пользователь
     */
    User update(Long id, User sourceUser);

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь был успешно удален, иначе false
     */
    boolean delete(Long id);

    /**
     * Получение списка всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> getAll();

    /**
     * Блокировка пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return true, если пользователь был успешно заблокирован, иначе false
     */
    boolean blockUser(Long userId);
}
