package com.my.repository;

import com.my.dto.ExpenseAnalysisDto;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    List<Transaction> getAll() throws SQLException;

    List<Transaction> getAll(TransactionFilter filter) throws SQLException;

    Optional<Transaction> getById(Long id) throws SQLException;

    Transaction save(Transaction entity) throws SQLException;

    Transaction update(Transaction entity) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    BigDecimal getMonthExpense(Long userId) throws SQLException;

    BigDecimal getBalance(Long userId) throws SQLException;

    BigDecimal getTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException;

    BigDecimal getTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException;

    List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException;

    BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId) throws SQLException;
}
