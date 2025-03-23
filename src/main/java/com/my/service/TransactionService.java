package com.my.service;

import com.my.dto.BalanceResponseDto;
import com.my.dto.ExpenseAnalysisDto;
import com.my.dto.ExpensesResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.dto.IncomeResponseDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
    List<TransactionResponseDto> getAll(TransactionFilter filter) throws SQLException;

    Transaction getEntityById(Long id) throws SQLException;

    /**
     * Получение транзакции по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return транзакция с указанным идентификатором
     */
    TransactionResponseDto getById(Long id) throws SQLException;

    /**
     * Сохранение новой транзакции.
     *
     * @param transaction объект транзакции для сохранения
     * @return сохраненная транзакция
     */
    TransactionResponseDto save(TransactionRequestDto transaction) throws SQLException;

    /**
     * Обновление существующей транзакции.
     *
     * @param id                идентификатор транзакции
     * @param sourceTransaction объект транзакции с обновленной информацией
     * @return обновленная транзакция
     */
    TransactionResponseDto update(Long id, TransactionRequestDto sourceTransaction) throws SQLException;

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
    ExpensesResponseDto getMonthExpense(Long userId) throws SQLException;

    /**
     * Проверка баланса пользователя.
     *
     * @param userId идентификатор пользователя
     * @return текущий баланс пользователя
     */
    BalanceResponseDto getBalance(Long userId) throws SQLException;

    /**
     * Расчет общего дохода пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода
     * @param to     дата окончания периода
     * @return общая сумма доходов за указанный период
     */
    IncomeResponseDto getTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Расчет общих расходов пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода
     * @param to     дата окончания периода
     * @return общая сумма расходов за указанный период
     */
    ExpensesResponseDto getTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Анализ расходов пользователя по категориям за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода
     * @param to     дата окончания периода
     * @return карта, где ключи - названия категорий, а значения - суммы расходов по этим категориям
     */
    List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Генерация финансового отчета пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param from   дата начала периода
     * @param to     дата окончания периода
     * @return карта с финансовым отчетом
     */
    FullReportResponseDto generateFinancialReport(Long userId, LocalDate from, LocalDate to) throws SQLException;

    /**
     * Обрабатывает транзакцию, проверяя, превышен ли бюджет для расходов
     * или достигнута ли цель для доходов. Возвращает список сообщений,
     * описывающих результаты проверки.
     *
     * @param transaction Транзакция, которую необходимо обработать.
     *                    Не должен быть null.
     * @return Сообщение, которое может содержать информацию о
     * превышении бюджета или достижении целей.
     * Если никаких сообщений нет, возвращается null.
     */
    String processTransaction(Transaction transaction) throws SQLException;
}
