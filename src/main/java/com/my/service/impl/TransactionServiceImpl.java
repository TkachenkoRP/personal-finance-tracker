package com.my.service.impl;

import com.my.mapper.TransactionMapper;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.TransactionRepository;
import com.my.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAll(TransactionFilter filter) {
        List<Transaction> transactions = transactionRepository.getAll();
        if (filter == null) {
            return transactions;
        }

        return transactions.stream()
                .filter(t -> filter.userId() == null || t.getUser().getId().equals(filter.userId()))
                .filter(t -> filter.categoryId() == null || t.getCategory().getId().equals(filter.categoryId()))
                .filter(t -> filter.date() == null || t.getDate().equals(filter.date()))
                .filter(t -> filter.type() == null || t.getType().equals(filter.type()))
                .toList();
    }

    @Override
    public Transaction getById(Long id) {
        return transactionRepository.getById(id).orElse(null);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Long id, Transaction sourceTransaction) {
        Transaction updatedTransaction = getById(id);
        if (updatedTransaction == null) {
            return null;
        }

        TransactionMapper.INSTANCE.updateTransaction(sourceTransaction, updatedTransaction);

        return transactionRepository.update(updatedTransaction);
    }

    @Override
    public boolean deleteById(Long id) {
        return transactionRepository.deleteById(id);
    }

    @Override
    public BigDecimal getMonthExpense(Long userId) {
        TransactionFilter filter = new TransactionFilter(userId, null, null, TransactionType.EXPENSE);
        List<Transaction> transactions = getAll(filter);
        return transactions.stream()
                .filter(t -> t.getDate().getMonth() == LocalDate.now().getMonth() && t.getDate().getYear() == LocalDate.now().getYear())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isBudgetExceeded(Long userId, BigDecimal budget) {
        BigDecimal monthExpense = getMonthExpense(userId);
        return monthExpense.compareTo(budget) > 0;
    }

    @Override
    public boolean isGoalIncome(Long userId, BigDecimal goal, Long transactionCategoryId) {
        BigDecimal goalExceeded = getGoalExceeded(userId, transactionCategoryId);
        return goalExceeded.compareTo(goal) > 0;
    }

    private BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId) {
        TransactionFilter transactionFilter = new TransactionFilter(userId, null, transactionCategoryId, TransactionType.INCOME);
        return getAll(transactionFilter).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}