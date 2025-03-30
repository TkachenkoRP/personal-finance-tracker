package com.my.service;

import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.dto.ReportResponseDto;
import com.my.model.Budget;

import java.util.List;

/**
 * Интерфейс BudgetService для управления бюджетами в системе.
 */
public interface BudgetService {
    /**
     * Получение всех бюджетов.
     *
     * @return список объектов BudgetResponseDto, представляющих все бюджеты
     */
    List<BudgetResponseDto> geAll();

    /**
     * Получение бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета
     * @return объект BudgetResponseDto, соответствующий указанному идентификатору
     */
    BudgetResponseDto getById(Long id);

    /**
     * Получение бюджета по его идентификатору в формате сущности.
     *
     * @param id идентификатор бюджета
     * @return объект Budget, соответствующий указанному идентификатору
     */
    Budget getEntityById(Long id);

    /**
     * Сохранение нового бюджета для указанного пользователя.
     *
     * @param userId идентификатор пользователя, которому принадлежит бюджет
     * @param budget объект BudgetRequestDto, содержащий данные для сохранения
     * @return объект BudgetResponseDto, представляющий сохранённый бюджет
     */
    BudgetResponseDto save(Long userId, BudgetRequestDto budget);

    /**
     * Обновление существующего бюджета.
     *
     * @param id           идентификатор бюджета, который нужно обновить
     * @param sourceBudget объект BudgetRequestDto с обновлёнными данными
     * @return объект BudgetResponseDto, представляющий обновлённый бюджет
     */
    BudgetResponseDto update(Long id, BudgetRequestDto sourceBudget);

    /**
     * Удаление бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для удаления
     * @return true, если бюджет был успешно удалён, иначе false
     */
    boolean deleteById(Long id);

    /**
     * Получение всех бюджетов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список объектов BudgetResponseDto, представляющих бюджеты пользователя
     */
    List<BudgetResponseDto> getAllBudgetsByUserId(Long userId);

    /**
     * Получение информации о превышении бюджета для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории бюджета
     * @return информация о превышении бюджета
     */
    ReportResponseDto getBudgetsExceededInfo(Long userId, Long categoryId);

    /**
     * Деактивация бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для деактивации
     * @return true, если бюджет был успешно деактивирован, иначе false
     */
    boolean deactivateBudget(Long id);
}
