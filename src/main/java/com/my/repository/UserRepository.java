package com.my.repository;

import com.my.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс UserRepository для работы с пользователями в системе.
 */
public interface UserRepository {
    /**
     * Получение всех пользователей.
     *
     * @return список всех пользователей
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<User> getAll() throws SQLException;

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект Optional, содержащий найденного пользователя, или пустой, если пользователь не найден
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<User> getById(Long id) throws SQLException;

    /**
     * Сохранение нового пользователя.
     *
     * @param entity объект пользователя для сохранения
     * @return сохраненный объект пользователя
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    User save(User entity) throws SQLException;

    /**
     * Обновление существующего пользователя.
     *
     * @param entity объект пользователя с обновленными данными
     * @return обновленный объект пользователя
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    User update(User entity) throws SQLException;

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     * @return true, если пользователь был успешно удален, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deleteById(Long id) throws SQLException;

    /**
     * Проверка существования пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты для проверки
     * @return true, если пользователь с указанным адресом электронной почты существует, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean isEmailAvailable(String email) throws SQLException;

    /**
     * Получение пользователя по адресу электронной почты и паролю.
     *
     * @param email    адрес электронной почты пользователя
     * @param password пароль пользователя
     * @return объект Optional, содержащий найденного пользователя, или пустой,
     * если пользователь с указанными учетными данными не найден
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<User> getByEmailAndPassword(String email, String password) throws SQLException;

    /**
     * Блокировка пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя для блокировки
     * @return true, если пользователь был успешно заблокирован, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean blockUserById(Long userId) throws SQLException;

    /**
     * Разблокировка пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя для разблокировки
     * @return true, если пользователь был успешно разблокирован, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean unBlockUserById(long userId) throws SQLException;
}
