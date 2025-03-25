package com.my.service;

import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.model.Budget;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс BudgetService для управления бюджетами в системе.
 */
public interface BudgetService {
    /**
     * Получение всех бюджетов.
     *
     * @return список объектов BudgetResponseDto, представляющих все бюджеты
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<BudgetResponseDto> geAll() throws SQLException;

    /**
     * Получение бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета
     * @return объект BudgetResponseDto, соответствующий указанному идентификатору
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    BudgetResponseDto getById(Long id) throws SQLException;

    /**
     * Получение бюджета по его идентификатору в формате сущности.
     *
     * @param id идентификатор бюджета
     * @return объект Budget, соответствующий указанному идентификатору
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Budget getEntityById(Long id) throws SQLException;

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
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    BudgetResponseDto update(Long id, BudgetRequestDto sourceBudget) throws SQLException;

    /**
     * Удаление бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для удаления
     * @return true, если бюджет был успешно удалён, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deleteById(Long id) throws SQLException;

    /**
     * Получение всех бюджетов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список объектов BudgetResponseDto, представляющих бюджеты пользователя
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<BudgetResponseDto> getAllBudgetsByUserId(Long userId) throws SQLException;

    /**
     * Получение информации о превышении бюджета для указанного пользователя и категории.
     *
     * @param userId     идентификатор пользователя
     * @param categoryId идентификатор категории бюджета
     * @return строка с информацией о превышении бюджета
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    String getBudgetsExceededInfo(Long userId, Long categoryId) throws SQLException;

    /**
     * Деактивация бюджета по его идентификатору.
     *
     * @param id идентификатор бюджета для деактивации
     * @return true, если бюджет был успешно деактивирован, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deactivateBudget(Long id) throws SQLException;
}
