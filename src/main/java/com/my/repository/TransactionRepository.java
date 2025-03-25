package com.my.repository;

import com.my.dto.ExpenseAnalysisDto;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;

import java.math.BigDecimal;
import java.sql.SQLException;
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
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<Transaction> getAll() throws SQLException;

    /**
     * Получение всех транзакций с применением фильтра.
     *
     * @param filter объект фильтра для применения к выборке транзакций
     * @return список транзакций, соответствующих фильтру
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<Transaction> getAll(TransactionFilter filter) throws SQLException;

    /**
     * Получение транзакции по ее идентификатору.
     *
     * @param id идентификатор транзакции
     * @return объект Optional, содержащий найденную транзакцию, или пустой, если транзакция не найдена
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<Transaction> getById(Long id) throws SQLException;

    /**
     * Сохранение новой транзакции.
     *
     * @param entity объект транзакции для сохранения
     * @return сохраненный объект транзакции
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Transaction save(Transaction entity) throws SQLException;

    /**
     * Обновление существующей транзакции.
     *
     * @param entity объект транзакции с обновленными данными
     * @return обновленный объект транзакции
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Transaction update(Transaction entity) throws SQLException;

    /**
     * Удаление транзакции по ее идентификатору.
     *
     * @param id идентификатор транзакции для удаления
     * @return true, если транзакция была успешно удалена, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deleteById(Long id) throws SQLException;

    /**
     * Получение общих расходов за месяц для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return общая сумма расходов за месяц
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    BigDecimal getMonthExpense(Long userId) throws SQLException;

    /**
     * Получение баланса для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return текущий баланс пользователя
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    BigDecimal getBalance(Long userId) throws SQLException;

    /**
     * Получение общей суммы доходов за указанный период для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная дата периода
     * @param to     конечная дата периода
     * @return общая сумма доходов за указанный период
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    BigDecimal getTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Получение общей суммы расходов за указанный период для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная дата периода
     * @param to     конечная дата периода
     * @return общая сумма расходов за указанный период
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    BigDecimal getTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Анализ расходов по категориям за указанный период для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная дата периода
     * @param to     конечная дата периода
     * @return список анализа расходов по категориям
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Получение суммы превышения цели для указанного пользователя и категории транзакции.
     *
     * @param userId                идентификатор пользователя
     * @param transactionCategoryId идентификатор категории транзакции
     * @return сумма превышения цели
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId) throws SQLException;
}
