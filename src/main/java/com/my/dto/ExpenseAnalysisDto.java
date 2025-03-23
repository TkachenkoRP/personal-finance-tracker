package com.my.dto;

import java.math.BigDecimal;

public record ExpenseAnalysisDto(String categoryName, BigDecimal totalExpenses) {
}
