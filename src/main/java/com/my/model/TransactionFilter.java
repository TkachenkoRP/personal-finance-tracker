package com.my.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilter {
    private Long userId;
    private LocalDate date;
    private LocalDate from;
    private LocalDate to;
    private Long categoryId;
    private TransactionType type;
}
