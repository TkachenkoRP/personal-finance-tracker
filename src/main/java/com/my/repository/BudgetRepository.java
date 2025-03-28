package com.my.repository;

import com.my.model.Budget;

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
     */
    List<Budget> getAll();

    /**
     * Получение бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета
     * @return объект Optional, содержащий найденный бюджет, или пустой, если бюджет не найден
     */
    Optional<Budget> getById(Long id);

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
     */
    Budget update(Budget entity);

    /**
     * Удаление бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для удаления
     * @return true, если бюджет был успешно удален, иначе false
     */
    boolean deleteById(Long id);

    /**
     * Получение всех бюджетов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список бюджетов, принадлежащих указанному пользователю
     */
    List<Budget> getAllByUserId(Long userId);

    /**
     * Получение активного бюджета для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории
     * @return объект Optional, содержащий найденный активный бюджет, или пустой, если бюджет не найден
     */
    Optional<Budget> getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Деактивация бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для деактивации
     * @return true, если бюджет был успешно деактивирован, иначе false
     */
    boolean deactivateBudgetById(Long id);
}
