package com.my.service.impl;

import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll_WithFilter() {
        User user = new User("user1@example.com", "password", "User One", UserRole.ROLE_USER);
        user.setId(1L);
        Transaction transaction1 = new Transaction();
        transaction1.setUser(user);
        transaction1.setType(TransactionType.EXPENSE);
        transaction1.setAmount(new BigDecimal("50.00"));
        transaction1.setDate(LocalDate.now());

        Transaction transaction2 = new Transaction();
        transaction2.setUser(user);
        transaction2.setType(TransactionType.INCOME);
        transaction2.setAmount(new BigDecimal("100.00"));
        transaction2.setDate(LocalDate.now());

        when(transactionRepository.getAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        TransactionFilter filter = new TransactionFilter(1L, null, null, null, null, null);
        List<Transaction> result = transactionService.getAll(filter);

        assertThat(result).hasSize(2);
    }

    @Test
    void testGetById_ExistingId() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));

        when(transactionRepository.getById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void testGetById_NonExistingId() {
        when(transactionRepository.getById(999L)).thenReturn(Optional.empty());

        Transaction result = transactionService.getById(999L);

        assertThat(result).isNull();
    }

    @Test
    void testSave() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.00"));

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.save(transaction);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void testUpdate_ExistingId() {
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(1L);
        existingTransaction.setAmount(new BigDecimal("100.00"));

        when(transactionRepository.getById(1L)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.update(existingTransaction)).thenReturn(existingTransaction);


        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(1L);
        updatedTransaction.setAmount(new BigDecimal("150.00"));

        Transaction result = transactionService.update(1L, updatedTransaction);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    void testUpdate_NonExistingId() {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(999L);
        updatedTransaction.setAmount(new BigDecimal("150.00"));

        when(transactionRepository.getById(999L)).thenReturn(Optional.empty());

        Transaction result = transactionService.update(999L, updatedTransaction);

        assertThat(result).isNull();
    }

    @Test
    void testDeleteById_ExistingId() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);

        when(transactionRepository.getById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.deleteById(1L)).thenReturn(true);

        boolean isDeleted = transactionService.deleteById(1L);

        assertThat(isDeleted).isTrue();
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_NonExistingId() {
        when(transactionRepository.getById(999L)).thenReturn(Optional.empty());

        boolean isDeleted = transactionService.deleteById(999L);

        assertThat(isDeleted).isFalse();
    }

    @Test
    void testGetMonthExpense() {
        User user = new User("user1@example.com", "password", "User One", UserRole.ROLE_USER);
        user.setId(1L);
        Transaction transaction1 = new Transaction();
        transaction1.setUser(user);
        transaction1.setType(TransactionType.EXPENSE);
        transaction1.setAmount(new BigDecimal("50.00"));
        transaction1.setDate(LocalDate.now());

        Transaction transaction2 = new Transaction();
        transaction2.setUser(user);
        transaction2.setType(TransactionType.EXPENSE);
        transaction2.setAmount(new BigDecimal("100.00"));
        transaction2.setDate(LocalDate.now());

        when(transactionRepository.getAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        BigDecimal monthExpense = transactionService.getMonthExpense(1L);

        assertThat(monthExpense).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    void testCalculateTotalIncome() {
        User user = new User("user1@example.com", "password", "User One", UserRole.ROLE_USER);
        user.setId(1L);
        Transaction transaction1 = new Transaction();
        transaction1.setUser(user);
        transaction1.setType(TransactionType.INCOME);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setDate(LocalDate.now());

        Transaction transaction2 = new Transaction();
        transaction2.setUser(user);
        transaction2.setType(TransactionType.INCOME);
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setDate(LocalDate.now());

        when(transactionRepository.getAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        BigDecimal totalIncome = transactionService.calculateTotalIncome(1L, LocalDate.now().minusDays(1), LocalDate.now());

        assertThat(totalIncome).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    void testCalculateTotalExpenses() {
        User user = new User("user1@example.com", "password", "User One", UserRole.ROLE_USER);
        user.setId(1L);
        Transaction transaction1 = new Transaction();
        transaction1.setUser(user);
        transaction1.setType(TransactionType.EXPENSE);
        transaction1.setAmount(new BigDecimal("50.00"));
        transaction1.setDate(LocalDate.now());

        Transaction transaction2 = new Transaction();
        transaction2.setUser(user);
        transaction2.setType(TransactionType.EXPENSE);
        transaction2.setAmount(new BigDecimal("100.00"));
        transaction2.setDate(LocalDate.now());

        when(transactionRepository.getAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        BigDecimal totalExpenses = transactionService.calculateTotalExpenses(1L, LocalDate.now().minusDays(1), LocalDate.now());

        assertThat(totalExpenses).isEqualTo(new BigDecimal("150.00"));
    }
}
