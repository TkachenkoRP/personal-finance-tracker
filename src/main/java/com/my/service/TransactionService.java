package com.my.service;

import com.my.model.Transaction;
import com.my.model.TransactionFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<Transaction> getAll(TransactionFilter filter);

    Transaction getById(Long id);

    Transaction save(Transaction transaction);

    Transaction update(Long id, Transaction sourceTransaction);

    boolean deleteById(Long id);

    BigDecimal getMonthExpense(Long userId);

    boolean isBudgetExceeded(Long userId, BigDecimal budget);

    boolean isGoalIncome(Long userId, BigDecimal goal, Long transactionCategoryId);

    BigDecimal checkBalance(Long userId);

    BigDecimal calculateTotalIncome(Long userId, LocalDate from, LocalDate to);

    BigDecimal calculateTotalExpenses(Long userId, LocalDate from, LocalDate to);

    Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to);

    Map<String, BigDecimal> generateFinancialReport(Long userId, LocalDate from, LocalDate to);
}
