package com.my.repository;

import com.my.model.Goal;

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
     */
    List<Goal> getAll();

    /**
     * Получение цели по ее идентификатору.
     *
     * @param id идентификатор цели
     * @return объект Optional, содержащий найденную цель, или пустой, если цель не найдена
     */
    Optional<Goal> getById(Long id);

    /**
     * Сохранение новой цели для указанного пользователя.
     *
     * @param userId идентификатор пользователя, которому принадлежит цель
     * @param entity объект цели для сохранения
     * @return сохраненный объект цели
     */
    Goal save(Long userId, Goal entity);

    /**
     * Обновление существующей цели.
     *
     * @param entity объект цели с обновленными данными
     * @return обновленный объект цели
     */
    Goal update(Goal entity);

    /**
     * Удаление цели по ее идентификатору.
     *
     * @param id идентификатор цели для удаления
     * @return true, если цель была успешно удалена, иначе false
     */
    boolean deleteById(Long id);

    /**
     * Получение всех целей для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список целей, принадлежащих указанному пользователю
     */
    List<Goal> getAllByUserId(Long userId);

    /**
     * Получение активной цели для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории
     * @return объект Optional, содержащий найденную активную цель, или пустой, если цель не найдена
     */
    Optional<Goal> getActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId);
}
