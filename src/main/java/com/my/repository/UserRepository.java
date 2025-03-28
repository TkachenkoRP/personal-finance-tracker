package com.my.repository;

import com.my.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс UserRepository для работы с пользователями в системе.
 */
public interface UserRepository {
    /**
     * Получение всех пользователей.
     */
    List<User> getAll();

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект Optional, содержащий найденного пользователя, или пустой, если пользователь не найден
     */
    Optional<User> getById(Long id);

    /**
     * Сохранение нового пользователя.
     *
     * @param entity объект пользователя для сохранения
     * @return сохраненный объект пользователя
     */
    User save(User entity);

    /**
     * Обновление существующего пользователя.
     *
     * @param entity объект пользователя с обновленными данными
     * @return обновленный объект пользователя
     */
    User update(User entity);

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     * @return true, если пользователь был успешно удален, иначе false
     */
    boolean deleteById(Long id);

    /**
     * Проверка существования пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты для проверки
     * @return true, если пользователь с указанным адресом электронной почты существует, иначе false
     */
    boolean isEmailOccupied(String email);

    /**
     * Получение пользователя по адресу электронной почты и паролю.
     *
     * @param email    адрес электронной почты пользователя
     * @param password пароль пользователя
     * @return объект Optional, содержащий найденного пользователя, или пустой,
     * если пользователь с указанными учетными данными не найден
     */
    Optional<User> getByEmailAndPassword(String email, String password);

    /**
     * Блокировка пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя для блокировки
     * @return true, если пользователь был успешно заблокирован, иначе false
     */
    boolean blockUserById(Long userId);

    /**
     * Разблокировка пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя для разблокировки
     * @return true, если пользователь был успешно разблокирован, иначе false
     */
    boolean unBlockUserById(long userId);
}
