package com.my.repository;

import com.my.model.Budget;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс BudgetRepository для работы с бюджетами.
 */
public interface BudgetRepository {
    /**
     * Получение всех бюджетов.
     *
     * @return список всех бюджетов
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<Budget> getAll() throws SQLException;

    /**
     * Получение бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета
     * @return объект Optional, содержащий найденный бюджет, или пустой, если бюджет не найден
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<Budget> getById(Long id) throws SQLException;

    /**
     * Сохранение нового бюджета для указанного пользователя.
     *
     * @param userId идентификатор пользователя, которому принадлежит бюджет
     * @param entity объект бюджета для сохранения
     * @return сохраненный объект бюджета
     */
    Budget save(Long userId, Budget entity);

    /**
     * Обновление существующего бюджета.
     *
     * @param entity объект бюджета с обновленными данными
     * @return обновленный объект бюджета
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Budget update(Budget entity) throws SQLException;

    /**
     * Удаление бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для удаления
     * @return true, если бюджет был успешно удален, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deleteById(Long id) throws SQLException;

    /**
     * Получение всех бюджетов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список бюджетов, принадлежащих указанному пользователю
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<Budget> getAllByUserId(Long userId) throws SQLException;

    /**
     * Получение активного бюджета для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории
     * @return объект Optional, содержащий найденный активный бюджет, или пустой, если бюджет не найден
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<Budget> getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;

    /**
     * Деактивация бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для деактивации
     * @return true, если бюджет был успешно деактивирован, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deactivateBudgetById(Long id) throws SQLException;
}
