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
public class BudgetRequestDto {
    private BigDecimal totalAmount;
    private String periodStart;
    private String periodEnd;
    private Long categoryId;
    private boolean active;
}
