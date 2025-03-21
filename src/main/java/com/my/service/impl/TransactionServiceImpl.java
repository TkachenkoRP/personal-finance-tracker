package com.my.service.impl;

import com.my.mapper.TransactionMapper;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.TransactionRepository;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.service.BudgetService;
import com.my.service.GoalService;
import com.my.service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionServiceImpl implements TransactionService {

    private final JdbcTransactionRepository transactionRepository;
    private final BudgetService budgetService;
    private final GoalService goalService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, BudgetService budgetService, GoalService goalService) {
        this.transactionRepository = (JdbcTransactionRepository) transactionRepository;
        this.budgetService = budgetService;
        this.goalService = goalService;
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
        Transaction saved = transactionRepository.save(transaction);
        processTransaction(saved);
        return saved;
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
        String msg = null;
        if (transaction.getType() == TransactionType.EXPENSE) {
            msg = budgetService.getBudgetsExceededInfo(transaction.getUser().getId(), transaction.getCategory().getId());
        } else {
            msg = goalService.getGoalIncomeInfo(transaction.getUser().getId(), transaction.getCategory().getId());
        }
        return msg;
    }
}