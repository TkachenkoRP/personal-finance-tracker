package com.my.repository.impl;

import com.my.model.Transaction;
import com.my.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class InMemoryTransactionRepositoryTest {
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = new InMemoryTransactionRepository();
    }

    private Transaction createTransaction(BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        return transactionRepository.save(transaction);
    }

    @Test
    void testGetAll() {
        createTransaction(BigDecimal.valueOf(100.0));
        createTransaction(BigDecimal.valueOf(200.0));
        List<Transaction> transactions = transactionRepository.getAll();
        assertThat(transactions).hasSize(2);
    }

    @Test
    void testGetById_ExistingId() {
        Transaction savedTransaction = createTransaction(BigDecimal.valueOf(100.0));
        Optional<Transaction> foundTransaction = transactionRepository.getById(savedTransaction.getId());
        assertThat(foundTransaction).isPresent();
        assertThat(foundTransaction.get().getAmount()).isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void testGetById_NonExistingId() {
        Optional<Transaction> foundTransaction = transactionRepository.getById(999L);
        assertThat(foundTransaction).isNotPresent();
    }

    @Test
    void testSave() {
        Transaction savedTransaction = createTransaction(BigDecimal.valueOf(100.0));
        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void testUpdate() {
        Transaction savedTransaction = createTransaction(BigDecimal.valueOf(100.0));
        savedTransaction.setAmount(BigDecimal.valueOf(150.0));
        Transaction updatedTransaction = transactionRepository.update(savedTransaction);
        assertThat(updatedTransaction.getAmount()).isEqualTo(BigDecimal.valueOf(150.0));
        Optional<Transaction> foundTransaction = transactionRepository.getById(savedTransaction.getId());
        assertThat(foundTransaction.get().getAmount()).isEqualTo(BigDecimal.valueOf(150.0));
    }

    @Test
    void testDeleteById_ExistingId() {
        Transaction savedTransaction = createTransaction(BigDecimal.valueOf(100.0));
        boolean isDeleted = transactionRepository.deleteById(savedTransaction.getId());
        assertThat(isDeleted).isTrue();
        assertThat(transactionRepository.getById(savedTransaction.getId())).isNotPresent();
    }

    @Test
    void testDeleteById_NonExistingId() {
        boolean isDeleted = transactionRepository.deleteById(999L);
        assertThat(isDeleted).isFalse();
    }
}