package com.my.util;

import com.my.dto.BudgetRequestDto;
import com.my.dto.GoalRequestDto;
import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.UserRegisterRequestDto;
import com.my.dto.UserRequestDto;
import com.my.exception.ArgumentNotValidException;
import com.my.model.TransactionType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

public class Validation {
    private Validation() {
    }

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

    public static void validationGoal(GoalRequestDto goal) throws ArgumentNotValidException {
        StringBuilder msg = new StringBuilder();
        if (goal.getTargetAmount() == null) {
            msg.append("Должна быть указана сумма цели");
        }
        if (goal.getCategoryId() == null || goal.getCategoryId() < 1) {
            msg.append("ID категории должно быть больше 0! ");
        }
        if (!msg.isEmpty()) {
            throw new ArgumentNotValidException(msg.toString());
        }
    }

    public static void validationTransaction(TransactionRequestDto transaction) throws ArgumentNotValidException {
        StringBuilder msg = new StringBuilder();
        if (transaction.getDate() == null || transaction.getDate().isBlank()) {
            msg.append("Должна быть указана дата транзакции! ");
        } else {
            try {
                LocalDate.parse(transaction.getDate(), DateTimeFormatter.ofPattern("d.M.yyyy"));
            } catch (DateTimeParseException e) {
                msg.append("Неверный формат даты (d.M.yyyy)! ");
            }
        }
        if (!msg.isEmpty()) {
            throw new ArgumentNotValidException(msg.toString());
        }
        if (transaction.getType() == null || transaction.getType().isBlank()) {
            msg.append("Должен быть указан тип транзакции! ");
        } else {
            List<String> acceptedValues = Stream.of(TransactionType.class.getEnumConstants()).map(Enum::name).toList();
            if (!acceptedValues.contains(transaction.getType())) {
                msg.append("Значение должно быть: ").append(String.join(", ", acceptedValues));
            }
        }
        if (transaction.getAmount() == null) {
            msg.append("Должен быть указана сумма (amount)! ");
        }
        if (transaction.getCategoryId() == null || transaction.getCategoryId() < 1) {
            msg.append("ID категории должно быть больше 0! ");
        }
        if (!msg.isEmpty()) {
            throw new ArgumentNotValidException(msg.toString());
        }
    }

    public static void validationCategory(TransactionCategoryRequestDto category) throws ArgumentNotValidException {
        StringBuilder msg = new StringBuilder();
        if (category.getCategoryName() == null || category.getCategoryName().isBlank()) {
            msg.append("Должно быть указано название категории! ");
        }
        if (!msg.isEmpty()) {
            throw new ArgumentNotValidException(msg.toString());
        }
    }

    public static void validationUserRegistry(UserRegisterRequestDto user) throws ArgumentNotValidException {
        StringBuilder msg = new StringBuilder();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            msg.append("Должен быть указан email! ");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            msg.append("Должно быть указано имя! ");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            msg.append("Должен быть указан пароль! ");
        }
        if (!msg.isEmpty()) {
            throw new ArgumentNotValidException(msg.toString());
        }
    }

    public static void validationUser(UserRequestDto user) throws ArgumentNotValidException {
        StringBuilder msg = new StringBuilder();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            msg.append("Должен быть указан email! ");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            msg.append("Должно быть указано имя! ");
        }
        if (!msg.isEmpty()) {
            throw new ArgumentNotValidException(msg.toString());
        }
    }
}
