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
public class GoalResponseDto {
    private Long id;
    private BigDecimal targetAmount;
    private TransactionCategoryResponseDto category;
    private boolean active;
}
