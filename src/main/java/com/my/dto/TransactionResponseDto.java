package com.my.dto;

import com.my.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransactionResponseDto {
    private Long id;
    private String date;
    private BigDecimal amount;
    private String description;
    private TransactionCategoryResponseDto category;
    private UserResponseDto user;
    private TransactionType type;
}
