package com.my.model;

import java.time.LocalDate;

public record TransactionFilter(
        Long userId,
        LocalDate date,
        LocalDate from,
        LocalDate to,
        Long categoryId,
        TransactionType type) {

    public TransactionFilter() {
        this(null, null, null, null, null, null);
    }

    public TransactionFilter(Long userId) {
        this(userId, null, null, null, null, null);
    }
}
