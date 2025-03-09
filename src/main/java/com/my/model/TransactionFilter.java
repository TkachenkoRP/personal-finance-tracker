package com.my.model;

import java.time.LocalDate;

public record TransactionFilter(
        Long userId,
        LocalDate date,
        TransactionCategory category,
        TransactionType type) {
}
