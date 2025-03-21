package com.my.service.impl;

import com.my.mapper.BudgetMapper;
import com.my.model.Budget;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.BudgetRepository;
import com.my.repository.TransactionRepository;
import com.my.service.BudgetService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Budget> geAll() throws SQLException {
        return budgetRepository.getAll();
    }

    @Override
    public Budget getById(Long id) throws SQLException {
        return budgetRepository.getById(id).orElse(null);
    }

    @Override
    public Budget create(Long userId, Budget goal) throws SQLException {
        return budgetRepository.save(userId, goal);
    }

    @Override
    public Budget update(Long id, Budget sourceBudget) throws SQLException {
        Budget updatedBudget = getById(id);
        if (updatedBudget == null) {
            return null;
        }

        BudgetMapper.INSTANCE.updateTransaction(sourceBudget, updatedBudget);

        return budgetRepository.update(updatedBudget);
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        return budgetRepository.deleteById(id);
    }

    @Override
    public List<Budget> getAllBudgetsByUserId(Long userId) throws SQLException {
        return budgetRepository.getAllByUserId(userId);
    }

    @Override
    public String getBudgetsExceededInfo(Long userId, Long categoryId) throws SQLException {
        Budget budget = getActiveBudgetByUserIdAndCategoryId(userId, categoryId);
        String result = null;
        if (budget != null) {
            TransactionFilter transactionFilter = new TransactionFilter(userId, null, budget.getPeriodStart(), budget.getPeriodEnd(), categoryId, TransactionType.EXPENSE);
            List<Transaction> transactions = transactionRepository.getAll(transactionFilter);
            BigDecimal sum = transactions.stream().map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (budget.getTotalAmount().compareTo(sum) >= 0) {
                result = MessageFormat
                        .format("#{0}: Перерасход для %name%! Ваши расходы: {1}, установленный бюджет: {2}",
                                budget.getId(), sum, budget.getTotalAmount());
            }
        }
        return result;
    }

    @Override
    public Budget getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException {
        return budgetRepository.getActiveBudgetByUserIdAndCategoryId(userId, categoryId).orElse(null);
    }
}
