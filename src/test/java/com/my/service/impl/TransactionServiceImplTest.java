package com.my.service.impl;

import com.my.dto.BalanceResponseDto;
import com.my.dto.ExpensesResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.dto.IncomeResponseDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.exception.EntityNotFoundException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

        List<TransactionResponseDto> transactions = transactionService.getAll(filter);

        assertThat(transactions).isNotNull().hasSize(1);
        verify(transactionRepository).getAll(filter);
    }

    @Test
    void testGetById_ExistingId() throws Exception {
        Long id = 1L;
        Transaction transaction = new Transaction();
        when(transactionRepository.getById(id)).thenReturn(Optional.of(transaction));

        TransactionResponseDto result = transactionService.getById(id);

        assertThat(result).isNotNull();
        verify(transactionRepository).getById(id);
    }

    @Test
    void testGetById_NonExistingId() throws Exception {
        Long id = 1L;
        when(transactionRepository.getById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class, () -> {
            transactionService.getById(id);
        });

        assertThat(thrown.getMessage()).contains("Транзакция с id 1 не найдена");
    }

    @Test
    void testUpdate_ExistingId() throws Exception {
        Long id = 1L;
        TransactionRequestDto sourceTransaction = new TransactionRequestDto();
        Transaction existingTransaction = new Transaction();

        when(transactionRepository.getById(id)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.update(existingTransaction)).thenReturn(existingTransaction);

        TransactionResponseDto result = transactionService.update(id, sourceTransaction);

        assertThat(result).isNotNull();
        verify(transactionRepository).update(existingTransaction);
    }

    @Test
    void testUpdate_NonExistingId() throws Exception {
        Long id = 1L;
        TransactionRequestDto sourceTransaction = new TransactionRequestDto();

        when(transactionRepository.getById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class, () -> {
            transactionService.update(id, sourceTransaction);
        });

        assertThat(thrown.getMessage()).contains("Транзакция с id 1 не найдена");
        verify(transactionRepository, never()).update(any());
    }

    @Test
    void testDeleteById_ExistingId() throws Exception {
        Long id = 1L;
        when(transactionRepository.deleteById(id)).thenReturn(true);

        boolean result = transactionService.deleteById(id);

        assertThat(result).isTrue();
        verify(transactionRepository).deleteById(id);
    }

    @Test
    void testDeleteById_NonExistingId() throws Exception {
        Long id = 1L;
        when(transactionRepository.deleteById(id)).thenReturn(false);

        boolean result = transactionService.deleteById(id);

        assertThat(result).isFalse();
        verify(transactionRepository).deleteById(id);
    }

    @Test
    void testGetMonthExpense() throws Exception {
        Long userId = 1L;
        BigDecimal expectedExpense = BigDecimal.valueOf(100);
        when(transactionRepository.getMonthExpense(userId)).thenReturn(expectedExpense);

        ExpensesResponseDto result = transactionService.getMonthExpense(userId);

        assertThat(result).isNotNull();
        assertThat(result.totalExpenses()).isEqualTo(expectedExpense);
    }

    @Test
    void testGetBalance() throws Exception {
        Long userId = 1L;
        BigDecimal expectedBalance = BigDecimal.valueOf(200);
        when(transactionRepository.getBalance(userId)).thenReturn(expectedBalance);

        BalanceResponseDto result = transactionService.getBalance(userId);

        assertThat(result).isNotNull();
        assertThat(result.balance()).isEqualTo(expectedBalance);
    }

    @Test
    void testGenerateFinancialReport() throws Exception {
        Long userId = 1L;
        LocalDate from = LocalDate.now().minusMonths(1);
        LocalDate to = LocalDate.now();

        when(transactionRepository.getTotalIncome(userId, from, to)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionRepository.getTotalExpenses(userId, from, to)).thenReturn(BigDecimal.valueOf(500));

        FullReportResponseDto report = transactionService.generateFinancialReport(userId, from, to);

        assertThat(report.income().totalIncome()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(report.expenses().totalExpenses()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(report.balance().balance()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void testGetTotalIncome() throws Exception {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);
        BigDecimal expectedIncome = BigDecimal.valueOf(1000.00);

        when(transactionRepository.getTotalIncome(userId, from, to)).thenReturn(expectedIncome);

        IncomeResponseDto actualIncome = transactionService.getTotalIncome(userId, from, to);

        assertThat(actualIncome).isNotNull();
        assertThat(actualIncome.totalIncome()).isEqualTo(expectedIncome);
    }

    @Test
    void testGetTotalExpenses() throws Exception {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);
        BigDecimal expectedExpenses = BigDecimal.valueOf(500.00);

        when(transactionRepository.getTotalExpenses(userId, from, to)).thenReturn(expectedExpenses);

        ExpensesResponseDto actualExpenses = transactionService.getTotalExpenses(userId, from, to);

        assertThat(actualExpenses).isNotNull();
        assertThat(actualExpenses.totalExpenses()).isEqualTo(expectedExpenses);
    }
}
