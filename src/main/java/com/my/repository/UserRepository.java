package com.my.repository;

import com.my.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll() throws SQLException;

    Optional<User> getById(Long id) throws SQLException;

    User save(User entity) throws SQLException;

    User update(User entity) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    /**
     * Проверка существования пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты для проверки
     * @return true, если пользователь с указанным адресом электронной почты существует, иначе false
     */
    boolean isEmailAvailable(String email) throws SQLException;

    /**
     * Получение пользователя по адресу электронной почты и паролю.
     *
     * @param email    адрес электронной почты пользователя
     * @param password пароль пользователя
     * @return объект Optional, содержащий найденного пользователя, или пустой,
     * если пользователь с указанными учетными данными не найден
     */
    Optional<User> getByEmailAndPassword(String email, String password) throws SQLException;

    boolean blockUserById(Long userId) throws SQLException;

    boolean unBlockUserById(long userId) throws SQLException;
}
