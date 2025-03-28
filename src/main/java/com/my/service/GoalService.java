package com.my.service;

import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.model.Goal;

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
     */
    Goal getEntityById(Long id);

    /**
     * Получение цели по её идентификатору в формате DTO.
     *
     * @param id идентификатор цели
     * @return объект GoalResponseDto, соответствующий указанному идентификатору
     */
    GoalResponseDto getById(Long id);

    /**
     * Сохранение новой цели для указанного пользователя.
     *
     * @param userId идентификатор пользователя, которому принадлежит цель
     * @param goal   объект GoalRequestDto, содержащий данные для сохранения
     * @return объект GoalResponseDto, представляющий сохранённую цель
     */
    GoalResponseDto save(Long userId, GoalRequestDto goal);

    /**
     * Обновление существующей цели.
     *
     * @param id         идентификатор цели, которую нужно обновить
     * @param sourceGoal объект GoalRequestDto с обновлёнными данными
     * @return объект GoalResponseDto, представляющий обновлённую цель
     */
    GoalResponseDto update(Long id, GoalRequestDto sourceGoal);

    /**
     * Удаление цели по её идентификатору.
     *
     * @param id идентификатор цели для удаления
     * @return true, если цель была успешно удалена, иначе false
     */
    boolean deleteById(Long id);

    /**
     * Получение всех целей для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список объектов GoalResponseDto, представляющих цели пользователя
     */
    List<GoalResponseDto> getAllGoalsByUserId(Long userId);

    /**
     * Получение информации о доходах от цели.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории цели
     * @return строка с информацией о доходах от цели
     */
    String getGoalIncomeInfo(Long userId, Long categoryId);

    /**
     * Получение всех активных целей для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории
     * @return объект Goal, представляющий активную цель
     */
    Goal getAllActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId);
}
