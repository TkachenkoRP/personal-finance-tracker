package com.my.service;

import com.my.model.Transaction;
import com.my.model.TransactionFilter;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    List<Transaction> getAll(TransactionFilter filter);

    Transaction getById(Long id);

    Transaction save(Transaction transaction);

    Transaction update(Long id, Transaction sourceTransaction);

    boolean deleteById(Long id);

    BigDecimal getMonthExpense(Long userId);

    boolean isBudgetExceeded(Long userId, BigDecimal budget);
}
