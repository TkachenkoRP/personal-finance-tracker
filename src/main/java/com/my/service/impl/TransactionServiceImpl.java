package com.my.service.impl;

import com.my.mapper.TransactionMapper;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.model.User;
import com.my.repository.TransactionRepository;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.service.TransactionService;
import com.my.service.UserService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionServiceImpl implements TransactionService {

    private final JdbcTransactionRepository transactionRepository;
    private final UserService userService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = (JdbcTransactionRepository) transactionRepository;
        this.userService = userService;
    }

    @Override
    public List<Transaction> getAll(TransactionFilter filter) throws SQLException {
        return transactionRepository.getAll(filter);
    }

    @Override
    public Transaction getById(Long id) throws SQLException {
        return transactionRepository.getById(id).orElse(null);
    }

    @Override
    public Transaction save(Transaction transaction) throws SQLException {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Long id, Transaction sourceTransaction) throws SQLException {
        Transaction updatedTransaction = getById(id);
        if (updatedTransaction == null) {
            return null;
        }

        TransactionMapper.INSTANCE.updateTransaction(sourceTransaction, updatedTransaction);

        return transactionRepository.update(updatedTransaction);
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        return transactionRepository.deleteById(id);
    }

    @Override
    public BigDecimal getMonthExpense(Long userId) throws SQLException {
        return transactionRepository.getMonthExpense(userId);
    }

    @Override
    public boolean isBudgetExceeded(Long userId, BigDecimal budget) throws SQLException {
        BigDecimal monthExpense = getMonthExpense(userId);
        return monthExpense.compareTo(budget) > 0;
    }

    @Override
    public boolean isGoalIncome(Long userId, BigDecimal goal, Long transactionCategoryId) throws SQLException {
        BigDecimal goalExceeded = getGoalExceeded(userId, transactionCategoryId);
        return goalExceeded.compareTo(goal) > 0;
    }

    @Override
    public BigDecimal getBalance(Long userId) throws SQLException {
        return transactionRepository.getBalance(userId);
    }

    @Override
    public BigDecimal getTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException {
        return transactionRepository.getTotalIncome(userId, from, to);
    }

    @Override
    public BigDecimal getTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException {
        return transactionRepository.getTotalExpenses(userId, from, to);
    }

    @Override
    public Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException {
        return transactionRepository.analyzeExpensesByCategory(userId, from, to);
    }

    @Override
    public Map<String, BigDecimal> generateFinancialReport(Long userId, LocalDate from, LocalDate to) throws SQLException {
        BigDecimal totalIncome = getTotalIncome(userId, from, to);
        BigDecimal totalExpenses = getTotalExpenses(userId, from, to);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("income", totalIncome);
        result.put("expenses", totalExpenses);
        result.put("balance", netBalance);
        return result;
    }

    @Override
    public String processTransaction(Transaction transaction) throws SQLException {
        String message;
        if (transaction.getType() == TransactionType.EXPENSE && transaction.getUser().getBudget() != null) {
            message = checkBudgetExceeded(transaction.getUser().getId(), transaction.getUser().getBudget());
        } else {
            message = checkGoalIncome(transaction.getCategory().getId(), transaction.getCategory().getCategoryName(), transaction.getUser());
        }
        return message;
    }

    private String checkBudgetExceeded(Long userId, BigDecimal budget) throws SQLException {
        String message = null;
        if (isBudgetExceeded(userId, budget)) {
            BigDecimal monthExpense = getMonthExpense(userId);
            message = "Перерасход бюджета! Ваши расходы: " + monthExpense +
                      ", установленный бюджет: " + budget;
        }
        return message;
    }

    private String checkGoalIncome(Long transactionCategoryId, String transactionCategoryName, User user) throws SQLException {
        String message = null;
        if (user.getGoals() != null && user.getGoals().get(transactionCategoryId) != null && isGoalIncome(user.getId(), user.getGoals().get(transactionCategoryId), transactionCategoryId)) {
            message = "Выполнена цель " + user.getGoals().get(transactionCategoryId) + " для " + transactionCategoryName;
            user.getGoals().remove(transactionCategoryId);
            userService.update(user.getId(), user);
        }
        return message;
    }

    private BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId) throws SQLException {
        return transactionRepository.getGoalExceeded(userId, transactionCategoryId);
    }
}