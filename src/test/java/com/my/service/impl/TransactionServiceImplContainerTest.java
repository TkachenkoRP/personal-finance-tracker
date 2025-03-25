package com.my.service.impl;

import com.my.dto.BalanceResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.AbstractTestContainer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionServiceImplContainerTest extends AbstractTestContainer {
    final Long userId = 2L;
    final Long userIdForAnalyze = 3L;
    final Long wrongId = 100L;

    @Test
    void whenGetTransactionById_withWrongId_thenReturnNull() {
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> transactionService.getById(wrongId));
        assertThat(thrown.getMessage()).isEqualTo("Транзакция с id 100 не найдена");
    }

    @Test
    void whenGetBalance_thenReturnCorrectBalance() throws Exception {
        BigDecimal expectedBalance = BigDecimal.valueOf(440.0);
        BalanceResponseDto actualBalance = transactionService.getBalance(userIdForAnalyze);
        assertThat(actualBalance.balance()).isEqualByComparingTo(expectedBalance);
    }

    @Test
    void whenGenerateFinancialReport_thenReturnCorrectMap() throws Exception {
        FullReportResponseDto report = transactionService.generateFinancialReport(userId, LocalDate.of(2025, 1, 1), LocalDate.now());
        assertAll(
                () -> assertThat(report.balance()).isNotNull(),
                () -> assertThat(report.expenses()).isNotNull(),
                () -> assertThat(report.balance()).isNotNull()
        );
    }
}
