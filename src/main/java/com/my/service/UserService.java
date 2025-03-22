package com.my.service;

import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.model.User;

import java.sql.SQLException;
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
    User registration(String email, String name, String password) throws SQLException;

    /**
     * Вход пользователя в систему.
     *
     * @param email адрес электронной почты пользователя
     * @param password пароль пользователя
     * @return пользователь, который вошел в систему
     */
    User login(String email, String password) throws SQLException;

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return пользователь с указанным идентификатором
     */
    UserResponseDto getById(Long id) throws SQLException;

    /**
     * Обновление информации о пользователе.
     *
     * @param id идентификатор пользователя
     * @param sourceUser объект пользователя с обновленной информацией
     * @return обновленный пользователь
     */
    UserResponseDto update(Long id, UserRequestDto sourceUser) throws SQLException;

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь был успешно удален, иначе false
     */
    boolean delete(Long id) throws SQLException;

    /**
     * Получение списка всех пользователей.
     *
     * @return список всех пользователей
     */
    List<UserResponseDto> getAll() throws SQLException;

    /**
     * Блокировка пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return true, если пользователь был успешно заблокирован, иначе false
     */
    boolean blockUser(Long userId) throws SQLException;
}
