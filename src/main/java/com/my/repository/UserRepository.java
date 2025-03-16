package com.my.repository;

import com.my.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления пользователями.
 * Наследует базовый интерфейс {@link BaseRepository} для выполнения основных операций
 * и добавляет специфичные для пользователей методы.
 */
public interface UserRepository extends BaseRepository<User> {
    /**
     * Проверка существования пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты для проверки
     * @return true, если пользователь с указанным адресом электронной почты существует, иначе false
     */
    boolean isPresentByEmail(String email) throws SQLException;

    /**
     * Получение пользователя по адресу электронной почты и паролю.
     *
     * @param email адрес электронной почты пользователя
     * @param password пароль пользователя
     * @return объект Optional, содержащий найденного пользователя, или пустой,
     * если пользователь с указанными учетными данными не найден
     */
    Optional<User> getByEmailAndPassword(String email, String password) throws SQLException;
}
