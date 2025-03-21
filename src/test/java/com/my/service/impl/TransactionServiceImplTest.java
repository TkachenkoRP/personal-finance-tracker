package com.my.service.impl;

import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.repository.impl.JdbcTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceImplTest {
    @Mock
    private JdbcTransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll_WithFilter() throws Exception {
        TransactionFilter filter = new TransactionFilter();
        Transaction transaction = new Transaction();
        when(transactionRepository.getAll(filter)).thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.getAll(filter);

        assertThat(transactions).isNotNull().hasSize(1);
    }

    @Test
    void testGetById_ExistingId() throws Exception {
        Long id = 1L;
        Transaction transaction = new Transaction();
        when(transactionRepository.getById(id)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getById(id);

        assertThat(result).isNotNull();
    }

    @Test
    void testGetById_NonExistingId() throws Exception {
        Long id = 1L;
        when(transactionRepository.getById(id)).thenReturn(Optional.empty());

        Transaction result = transactionService.getById(id);

        assertThat(result).isNull();
    }

    @Test
    void testSave() throws Exception {
        Transaction transaction = new Transaction();
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.save(transaction);

        assertThat(result).isNotNull();
    }

    @Test
    void testUpdate_ExistingId() throws Exception {
        Long id = 1L;
        Transaction sourceTransaction = new Transaction();
        Transaction updatedTransaction = new Transaction();
        when(transactionRepository.getById(id)).thenReturn(Optional.of(updatedTransaction));
        when(transactionRepository.update(updatedTransaction)).thenReturn(updatedTransaction);

        Transaction result = transactionService.update(id, sourceTransaction);

        assertThat(result).isNotNull();
    }

    @Test
    void testUpdate_NonExistingId() throws Exception {
        Long id = 1L;
        Transaction sourceTransaction = new Transaction();
        when(transactionRepository.getById(id)).thenReturn(Optional.empty());

        Transaction result = transactionService.update(id, sourceTransaction);

        assertThat(result).isNull();
    }

    @Test
    void testDeleteById_ExistingId() throws Exception {
        Long id = 1L;
        when(transactionRepository.deleteById(id)).thenReturn(true);

        boolean result = transactionService.deleteById(id);

        assertThat(result).isTrue();
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_NonExistingId() throws Exception {
        Long id = 1L;
        when(transactionRepository.deleteById(id)).thenReturn(false);

        boolean result = transactionService.deleteById(id);

        assertThat(result).isFalse();
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void testGetMonthExpense() throws Exception {
        Long userId = 1L;
        BigDecimal expectedExpense = BigDecimal.valueOf(100);
        when(transactionRepository.getMonthExpense(userId)).thenReturn(expectedExpense);

        BigDecimal result = transactionService.getMonthExpense(userId);

        assertThat(result).isEqualTo(new BigDecimal("100"));
    }

    @Test
    void testGetBalance() throws Exception {
        Long userId = 1L;
        BigDecimal expectedBalance = BigDecimal.valueOf(200);
        when(transactionRepository.getBalance(userId)).thenReturn(expectedBalance);

        BigDecimal result = transactionService.getBalance(userId);

        assertThat(result).isEqualTo(expectedBalance);
    }

    @Test
    void testGenerateFinancialReport() throws Exception {
        Long userId = 1L;
        LocalDate from = LocalDate.now().minusMonths(1);
        LocalDate to = LocalDate.now();
        when(transactionRepository.getTotalIncome(userId, from, to)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionRepository.getTotalExpenses(userId, from, to)).thenReturn(BigDecimal.valueOf(500));

        Map<String, BigDecimal> report = transactionService.generateFinancialReport(userId, from, to);
        assertThat(report.get("income")).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(report.get("expenses")).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(report.get("balance")).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void testGetTotalIncome() throws Exception {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);
        BigDecimal expectedIncome = new BigDecimal("1000.00");

        when(transactionRepository.getTotalIncome(userId, from, to)).thenReturn(expectedIncome);

        BigDecimal actualIncome = transactionService.getTotalIncome(userId, from, to);

        assertThat(actualIncome).isEqualTo(expectedIncome);
    }

    @Test
    void testGetTotalExpenses() throws Exception {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);
        BigDecimal expectedExpenses = new BigDecimal("500.00");

        when(transactionRepository.getTotalExpenses(userId, from, to)).thenReturn(expectedExpenses);

        BigDecimal actualExpenses = transactionService.getTotalExpenses(userId, from, to);

        assertThat(actualExpenses).isEqualTo(expectedExpenses);
    }
}
