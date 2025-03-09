package com.my.model;

import java.time.LocalDate;

public record TransactionFilter(
        Long userId,
        LocalDate date,
        Long categoryId,
        TransactionType type) {
}
