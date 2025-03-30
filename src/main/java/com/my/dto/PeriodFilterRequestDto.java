package com.my.dto;

import java.time.LocalDate;

public record PeriodFilterRequestDto(
        LocalDate from,
        LocalDate to
) {
}
