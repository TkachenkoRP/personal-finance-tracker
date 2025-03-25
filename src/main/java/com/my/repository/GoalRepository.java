package com.my.repository;

import com.my.model.Goal;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс GoalRepository для работы с целями.
 */
public interface GoalRepository {
    /**
     * Получение всех целей.
     *
     * @return список всех целей
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<Goal> getAll() throws SQLException;

    /**
     * Получение цели по ее идентификатору.
     *
     * @param id идентификатор цели
     * @return объект Optional, содержащий найденную цель, или пустой, если цель не найдена
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<Goal> getById(Long id) throws SQLException;

    /**
     * Сохранение новой цели для указанного пользователя.
     *
     * @param userId идентификатор пользователя, которому принадлежит цель
     * @param entity объект цели для сохранения
     * @return сохраненный объект цели
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Goal save(Long userId, Goal entity) throws SQLException;

    /**
     * Обновление существующей цели.
     *
     * @param entity объект цели с обновленными данными
     * @return обновленный объект цели
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Goal update(Goal entity) throws SQLException;

    /**
     * Удаление цели по ее идентификатору.
     *
     * @param id идентификатор цели для удаления
     * @return true, если цель была успешно удалена, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deleteById(Long id) throws SQLException;

    /**
     * Получение всех целей для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список целей, принадлежащих указанному пользователю
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<Goal> getAllByUserId(Long userId) throws SQLException;

    /**
     * Получение активной цели для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории
     * @return объект Optional, содержащий найденную активную цель, или пустой, если цель не найдена
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<Goal> getActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;
}
