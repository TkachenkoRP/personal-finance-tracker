package com.my.service.impl;

import com.my.model.Transaction;
import com.my.model.TransactionCategory;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.service.AbstractTestContainer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TransactionServiceImplContainerTest extends AbstractTestContainer {
    final Long userId = 2L;
    final Long userIdForAnalyze = 3L;
    final int countTransactionsForUser = 6;
    final Long wrongId = 100L;

    @Test
    void whenGetAllTransactions_thenReturnAllTransactions() throws Exception {
        int count = transactionRepository.getAll().size();

        List<Transaction> transactions = transactionService.getAll(new TransactionFilter());

        assertThat(transactions).hasSize(count);
    }

    @Test
    void whenGetAllTransactions_withFilterUserId_thenReturnAllTransactions() throws Exception {
        TransactionFilter filter = new TransactionFilter(userIdForAnalyze);
        List<Transaction> transactions = transactionService.getAll(filter);

        assertThat(transactions).hasSize(countTransactionsForUser);
    }

    @Test
    void whenGetTransactionById_thenReturnTransaction() throws Exception {
        Transaction transaction = transactionService.getById(1L);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isEqualTo(1L);
    }

    @Test
    void whenGetTransactionById_withWrongId_thenReturnNull() throws Exception {
        Transaction transaction = transactionService.getById(wrongId);

        assertThat(transaction).isNull();
    }

    @Test
    void whenSaveTransaction_thenTransactionIsSaved() throws Exception {
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(BigDecimal.valueOf(100));
        newTransaction.setUser(userService.getById(1L));
        newTransaction.setDate(LocalDate.now());
        newTransaction.setType(TransactionType.INCOME);
        TransactionCategory transactionCategory = transactionCategoryService.getById(1L);
        newTransaction.setCategory(transactionCategory);

        Transaction savedTransaction = transactionService.save(newTransaction);
        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualTo(new BigDecimal("100"));
        assertThat(savedTransaction.getUser().getId()).isEqualTo(1L);
    }

    @Test
    void whenUpdateTransaction_thenTransactionIsUpdated() throws Exception {
        Transaction existingTransaction = transactionService.getById(1L);
        existingTransaction.setAmount(BigDecimal.valueOf(200));

        Transaction updatedTransaction = transactionService.update(existingTransaction.getId(), existingTransaction);
        assertThat(updatedTransaction).isNotNull();
        assertThat(updatedTransaction.getId()).isEqualTo(1L);
        assertThat(updatedTransaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void whenDeleteTransaction_thenReturnTrue() throws Exception {
        Transaction deletedTransaction = transactionService.getById(6L);
        assertThat(deletedTransaction).isNotNull();

        boolean isDeleted = transactionService.deleteById(6L);
        assertThat(isDeleted).isTrue();

        deletedTransaction = transactionService.getById(6L);
        assertThat(deletedTransaction).isNull();
    }

    @Test
    void whenGetMonthExpense_thenReturnAmount() throws Exception {
        Transaction transaction = new Transaction(LocalDate.now(), TransactionType.EXPENSE, new BigDecimal(700), "desc", transactionCategoryService.getById(1L));
        transaction.setUser(userService.getById(2L));
        transactionService.save(transaction);
        BigDecimal expectedExpense = BigDecimal.valueOf(700.00);
        BigDecimal actualExpense = transactionService.getMonthExpense(userId);
        assertThat(actualExpense).isEqualByComparingTo(expectedExpense);
    }

    @Test
    void whenIsBudgetExceeded_andBudgetIsExceeded_thenReturnTrue() throws Exception {
        BigDecimal budget = BigDecimal.valueOf(80);
        Transaction transaction = new Transaction(LocalDate.now(), TransactionType.EXPENSE, new BigDecimal(100), "desc", transactionCategoryService.getById(1L));
        transaction.setUser(userService.getById(2L));
        transactionService.save(transaction);
        boolean isExceeded = transactionService.isBudgetExceeded(userId, budget);
        assertThat(isExceeded).isTrue();
    }

    @Test
    void whenIsGoalIncome_andGoalIsExceeded_thenReturnTrue() throws Exception {
        BigDecimal goal = BigDecimal.valueOf(100);
        boolean isGoalExceeded = transactionService.isGoalIncome(userId, goal, 1L);
        assertThat(isGoalExceeded).isTrue();
    }

    @Test
    void whenGetBalance_thenReturnCorrectBalance() throws Exception {
        BigDecimal expectedBalance = BigDecimal.valueOf(440.0);
        BigDecimal actualBalance = transactionService.getBalance(userIdForAnalyze);
        assertThat(actualBalance).isEqualByComparingTo(expectedBalance);
    }

    @Test
    void whenGenerateFinancialReport_thenReturnCorrectMap() throws Exception {
        Map<String, BigDecimal> report = transactionService.generateFinancialReport(userId, LocalDate.of(2025, 1, 1), LocalDate.now());
        assertAll(
                () -> assertThat(report).containsKey("income"),
                () -> assertThat(report).containsKey("expenses"),
                () -> assertThat(report).containsKey("balance")
        );
    }
}
