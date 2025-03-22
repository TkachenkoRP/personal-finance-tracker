package com.my.util;

import com.my.dto.BudgetRequestDto;
import com.my.exception.ArgumentNotValidException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validation {
    public static void validationBudget(BudgetRequestDto budget) throws ArgumentNotValidException {
        StringBuilder msg = new StringBuilder();
        if (budget.getTotalAmount() == null) {
            msg.append("Должен быть указана сумма (amount)! ");
        }
        if (budget.getPeriodStart() == null || budget.getPeriodStart().isBlank()) {
            msg.append("Должно быть указано начало периода! ");
        } else {
            try {
                LocalDate.parse(budget.getPeriodStart(), DateTimeFormatter.ofPattern("d.M.yyyy"));
            } catch (DateTimeParseException e) {
                msg.append("Неверный формат даты (d.M.yyyy)! ");
            }
        }
        if (budget.getPeriodEnd() == null || budget.getPeriodEnd().isBlank()) {
            msg.append("Должен быть указан конец периода! ");
        } else {
            try {
                LocalDate.parse(budget.getPeriodEnd(), DateTimeFormatter.ofPattern("d.M.yyyy"));
            } catch (DateTimeParseException e) {
                msg.append("Неверный формат даты (d.M.yyyy)! ");
            }
        }
        if (budget.getCategoryId() == null || budget.getCategoryId() < 1) {
            msg.append("ID категории должно быть больше 0! ");
        }
        if (!msg.isEmpty()) {
            throw new ArgumentNotValidException(msg.toString());
        }
    }
}
