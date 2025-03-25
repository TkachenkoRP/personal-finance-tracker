package com.my.service;

import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.model.Goal;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс GoalService для управления целями в системе.
 */
public interface GoalService {
    /**
     * Получение цели по её идентификатору.
     *
     * @param id идентификатор цели
     * @return объект Goal, соответствующий указанному идентификатору
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Goal getEntityById(Long id) throws SQLException;

    /**
     * Получение цели по её идентификатору в формате DTO.
     *
     * @param id идентификатор цели
     * @return объект GoalResponseDto, соответствующий указанному идентификатору
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    GoalResponseDto getById(Long id) throws SQLException;

    /**
     * Сохранение новой цели для указанного пользователя.
     *
     * @param userId идентификатор пользователя, которому принадлежит цель
     * @param goal   объект GoalRequestDto, содержащий данные для сохранения
     * @return объект GoalResponseDto, представляющий сохранённую цель
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    GoalResponseDto save(Long userId, GoalRequestDto goal) throws SQLException;

    /**
     * Обновление существующей цели.
     *
     * @param id         идентификатор цели, которую нужно обновить
     * @param sourceGoal объект GoalRequestDto с обновлёнными данными
     * @return объект GoalResponseDto, представляющий обновлённую цель
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    GoalResponseDto update(Long id, GoalRequestDto sourceGoal) throws SQLException;

    /**
     * Удаление цели по её идентификатору.
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
     * @return список объектов GoalResponseDto, представляющих цели пользователя
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<GoalResponseDto> getAllGoalsByUserId(Long userId) throws SQLException;

    /**
     * Получение информации о доходах от цели.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории цели
     * @return строка с информацией о доходах от цели
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    String getGoalIncomeInfo(Long userId, Long categoryId) throws SQLException;

    /**
     * Получение всех активных целей для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории
     * @return объект Goal, представляющий активную цель
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Goal getAllActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;
}
