package com.my.repository;

import com.my.dto.ExpenseAnalysisDto;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс TransactionRepository для работы с транзакциями.
 */
public interface TransactionRepository {
    /**
     * Получение всех транзакций.
     *
     * @return список всех транзакций
     */
    List<Transaction> getAll();

    /**
     * Получение всех транзакций с применением фильтра.
     *
     * @param filter объект фильтра для применения к выборке транзакций
     * @return список транзакций, соответствующих фильтру
     */
    List<Transaction> getAll(TransactionFilter filter);

    /**
     * Получение транзакции по ее идентификатору.
     *
     * @param id идентификатор транзакции
     * @return объект Optional, содержащий найденную транзакцию, или пустой, если транзакция не найдена
     */
    Optional<Transaction> getById(Long id);

    /**
     * Сохранение новой транзакции.
     *
     * @param entity объект транзакции для сохранения
     * @return сохраненный объект транзакции
     */
    Transaction save(Transaction entity);

    /**
     * Обновление существующей транзакции.
     *
     * @param entity объект транзакции с обновленными данными
     * @return обновленный объект транзакции
     */
    Transaction update(Transaction entity);

    /**
     * Удаление транзакции по ее идентификатору.
     *
     * @param id идентификатор транзакции для удаления
     * @return true, если транзакция была успешно удалена, иначе false
     */
    boolean deleteById(Long id);

    /**
     * Получение общих расходов за месяц для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return общая сумма расходов за месяц
     */
    BigDecimal getMonthExpense(Long userId);

    /**
     * Получение баланса для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return текущий баланс пользователя
     */
    BigDecimal getBalance(Long userId);

    /**
     * Получение общей суммы доходов за указанный период для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная дата периода
     * @param to     конечная дата периода
     * @return общая сумма доходов за указанный период
     */
    BigDecimal getTotalIncome(Long userId, LocalDate from, LocalDate to);

    /**
     * Получение общей суммы расходов за указанный период для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная дата периода
     * @param to     конечная дата периода
     * @return общая сумма расходов за указанный период
     */
    BigDecimal getTotalExpenses(Long userId, LocalDate from, LocalDate to);

    /**
     * Анализ расходов по категориям за указанный период для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная дата периода
     * @param to     конечная дата периода
     * @return список анализа расходов по категориям
     */
    List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to);

    /**
     * Получение суммы превышения цели для указанного пользователя и категории транзакции.
     *
     * @param userId                идентификатор пользователя
     * @param transactionCategoryId идентификатор категории транзакции
     * @return сумма превышения цели
     */
    BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId);
}
