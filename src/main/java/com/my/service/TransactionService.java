package com.my.service;

import com.my.model.Transaction;
import com.my.model.TransactionFilter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для управления финансовыми транзакциями.
 */
public interface TransactionService {
    /**
     * Получение списка всех транзакций с применением фильтрации.
     *
     * @param filter фильтр для выборки транзакций
     * @return список транзакций, соответствующих фильтру
     */
    List<Transaction> getAll(TransactionFilter filter) throws SQLException;

    /**
     * Получение транзакции по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return транзакция с указанным идентификатором
     */
    Transaction getById(Long id) throws SQLException;

    /**
     * Сохранение новой транзакции.
     *
     * @param transaction объект транзакции для сохранения
     * @return сохраненная транзакция
     */
    Transaction save(Transaction transaction) throws SQLException;

    /**
     * Обновление существующей транзакции.
     *
     * @param id идентификатор транзакции
     * @param sourceTransaction объект транзакции с обновленной информацией
     * @return обновленная транзакция
     */
    Transaction update(Long id, Transaction sourceTransaction) throws SQLException;

    /**
     * Удаление транзакции по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return true, если транзакция была успешно удалена, иначе false
     */
    boolean deleteById(Long id) throws SQLException;

    /**
     * Получение месячных расходов пользователя.
     *
     * @param userId идентификатор пользователя
     * @return общая сумма расходов за месяц
     */
    BigDecimal getMonthExpense(Long userId) throws SQLException;

    /**
     * Проверка превышения бюджета пользователя.
     *
     * @param userId идентификатор пользователя
     * @param budget бюджет для проверки
     * @return true, если бюджет превышен, иначе false
     */
    boolean isBudgetExceeded(Long userId, BigDecimal budget) throws SQLException;

    /**
     * Проверка достижения целевого дохода для пользователя.
     *
     * @param userId идентификатор пользователя
     * @param goal целевой доход
     * @param transactionCategoryId идентификатор категории транзакции
     * @return true, если целевой доход достигнут, иначе false
     */
    boolean isGoalIncome(Long userId, BigDecimal goal, Long transactionCategoryId) throws SQLException;

    /**
     * Проверка баланса пользователя.
     *
     * @param userId идентификатор пользователя
     * @return текущий баланс пользователя
     */
    BigDecimal getBalance(Long userId) throws SQLException;

    /**
     * Расчет общего дохода пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from дата начала периода
     * @param to дата окончания периода
     * @return общая сумма доходов за указанный период
     */
    BigDecimal getTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Расчет общих расходов пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from дата начала периода
     * @param to дата окончания периода
     * @return общая сумма расходов за указанный период
     */
    BigDecimal getTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Анализ расходов пользователя по категориям за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from дата начала периода
     * @param to дата окончания периода
     * @return карта, где ключи - названия категорий, а значения - суммы расходов по этим категориям
     */
    Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Генерация финансового отчета пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from дата начала периода
     * @param to дата окончания периода
     * @return карта с финансовым отчетом
     */
    Map<String, BigDecimal> generateFinancialReport(Long userId, LocalDate from, LocalDate to) throws SQLException;
}
