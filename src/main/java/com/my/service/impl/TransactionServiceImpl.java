package com.my.service.impl;

import com.my.mapper.TransactionMapper;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.TransactionRepository;
import com.my.service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAll(TransactionFilter filter) throws SQLException {
        List<Transaction> transactions = transactionRepository.getAll();
        if (filter == null) {
            return transactions;
        }

        return transactions.stream()
                .filter(t -> filter.userId() == null || t.getUser().getId().equals(filter.userId()))
                .filter(t -> filter.categoryId() == null || t.getCategory().getId().equals(filter.categoryId()))
                .filter(t -> filter.date() == null || t.getDate().equals(filter.date()))
                .filter(t -> filter.from() == null || !t.getDate().isBefore(filter.from()))
                .filter(t -> filter.to() == null || !t.getDate().isAfter(filter.to()))
                .filter(t -> filter.type() == null || t.getType().equals(filter.type()))
                .toList();
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
        List<Transaction> transactions = getAll(new TransactionFilter(userId, null, null, null, null, TransactionType.EXPENSE));
        return transactions.stream()
                .filter(t -> t.getDate().getMonth() == LocalDate.now().getMonth() && t.getDate().getYear() == LocalDate.now().getYear())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
    public BigDecimal checkBalance(Long userId) throws SQLException {
        List<Transaction> transactions = getAll(new TransactionFilter(userId, null, null, null, null, null));
        BigDecimal result = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                result = result.add(t.getAmount());
            } else {
                result = result.subtract(t.getAmount());
            }
        }
        return result;
    }

    @Override
    public BigDecimal calculateTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException {
        List<Transaction> transactions = getAll(new TransactionFilter(userId, null, from, to, null, TransactionType.INCOME));
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME &&
                        !t.getDate().isBefore(from) &&
                        !t.getDate().isAfter(to))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException {
        List<Transaction> transactions = getAll(new TransactionFilter(userId, null, from, to, null, TransactionType.EXPENSE));
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE &&
                        !t.getDate().isBefore(from) &&
                        !t.getDate().isAfter(to))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException {
        Map<String, BigDecimal> result = new HashMap<>();
        List<Transaction> transactions = getAll(new TransactionFilter(userId, null, from, to, null, TransactionType.EXPENSE));
        transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE &&
                        !t.getDate().isBefore(from) &&
                        !t.getDate().isAfter(to))
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)))
                .forEach(((transactionCategory, total) ->
                        result.put(transactionCategory.getCategoryName(), total)));
        return result;
    }

    @Override
    public Map<String, BigDecimal> generateFinancialReport(Long userId, LocalDate from, LocalDate to) throws SQLException {
        BigDecimal totalIncome = calculateTotalIncome(userId, from, to);
        BigDecimal totalExpenses = calculateTotalExpenses(userId, from, to);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("income", totalIncome);
        result.put("expenses", totalExpenses);
        result.put("balance", netBalance);
        return result;
    }

    private BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId) throws SQLException {
        TransactionFilter transactionFilter = new TransactionFilter(userId, null, null, null, transactionCategoryId, TransactionType.INCOME);
        return getAll(transactionFilter).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}