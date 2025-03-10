package com.my.model;

import java.time.LocalDate;

public record TransactionFilter(
        Long userId,
        LocalDate date,
        LocalDate from,
        LocalDate to,
        Long categoryId,
        TransactionType type) {
}
