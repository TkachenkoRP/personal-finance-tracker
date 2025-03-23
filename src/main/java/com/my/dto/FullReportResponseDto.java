package com.my.dto;

public record FullReportResponseDto(IncomeResponseDto income,
                                    ExpensesResponseDto expenses,
                                    BalanceResponseDto balance) {
}
