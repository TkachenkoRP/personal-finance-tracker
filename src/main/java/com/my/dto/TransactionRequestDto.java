package com.my.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransactionRequestDto {
    private String date;
    private String type;
    private BigDecimal amount;
    private String description;
    private Long categoryId;
}
