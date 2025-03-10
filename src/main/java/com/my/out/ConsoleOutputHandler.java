package com.my.out;

import com.my.model.Transaction;
import com.my.model.TransactionCategory;
import com.my.model.User;
import com.my.model.UserRole;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConsoleOutputHandler {

    private ConsoleOutputHandler() {
    }

    public static void displayMenu(User currentUser) {
        System.out.println("Доступные команды:");
        if (currentUser == null) {
            System.out.println("1 - регистрация пользователя;");
            System.out.println("2 - авторизация пользователя;");
            System.out.println("0 - завершить работу.\n");
        } else {
            System.out.println(currentUser.getName());
            System.out.println("3 - редактировать профиль.");
            System.out.println("4 - удалить текущего пользователя.");
            System.out.println("5 - отобразить все категории.");
            System.out.println("6 - добавить категорию.");
            System.out.println("7 - редактировать категорию.");
            System.out.println("8 - отобразить все транзакции.");
            System.out.println("9 - добавить транзакцию.");
            System.out.println("10 - редактировать транзакцию.");
            System.out.println("11 - удалить транзакцию.");
            System.out.println("12 - отобразить бюджет.");
            System.out.println("13 - отредактировать бюджет.");
            System.out.println("14 - отобразить цель.");
            System.out.println("15 - отредактировать цель.");
            System.out.println("16 - отобразить баланс.");
            System.out.println("17 - отобразить баланс в периоде.");
            System.out.println("18 - отобразить доход в периоде.");
            System.out.println("19 - отобразить расход в периоде.");
            System.out.println("20 - отобразить расход по категориям.");
            System.out.println("21 - отобразить финансовый отчет.");
            System.out.println("0 - выход.");
        }

        if (currentUser != null && currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            System.out.println("\nПанель управления для администратора:");
            System.out.println("22 - отобразить всех пользователей.");
            System.out.println("23 - редактировать пользователя.");
            System.out.println("24 - удалить пользователя.");
            System.out.println("25 - удалить категорию.");
            System.out.println("26 - заблокировать пользователя.");
        }
    }

    public static void displayMsg(String msg) {
        System.out.println(msg);
    }

    public static void displayUserList(List<User> users) {
        users.forEach(u -> System.out.println(u.getId() + " - " + u.getEmail() + " - " + u.getName()));
    }

    public static void displayTransactionList(List<Transaction> transactions) {
        transactions.forEach(t ->
                System.out.println(MessageFormat.format("{0} - {1}, {2}, {3}, {4}, {5}",
                        t.getId(),
                        t.getDate(),
                        t.getType(),
                        t.getAmount(),
                        t.getCategory().getCategoryName(),
                        t.getUser().getEmail())));
    }

    public static void displayTransactionCategoryList(List<TransactionCategory> transactionsCategories) {
        transactionsCategories.forEach(t ->
                System.out.println(MessageFormat.format("{0} - {1}",
                        t.getId(),
                        t.getCategoryName())));
    }

    public static void displayEnum(Class<? extends Enum<?>> enumClass) {
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        for (Enum<?> constant : enumConstants) {
            System.out.println(constant.ordinal() + " - " + constant);
        }
    }

    public static void displayMapWithCategories(Map<Long, BigDecimal> map, List<TransactionCategory> categories) {
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(TransactionCategory::getId, TransactionCategory::getCategoryName));

        map.forEach((key, value) -> {
                    String categoryName = categoryMap.get(key);
                    if (categoryName != null) {
                        System.out.println(key + " - " + categoryName + " - " + value);
                    } else {
                        System.out.println(key + " - Категория не найдена - " + value);
                    }
                }
        );

        System.out.println("\nНеиспользуемые категории:");
        categoryMap.forEach((id, name) -> {
            if (!map.containsKey(id)) {
                System.out.println(id + " - " + name);
            }
        });
    }

    public static void displayMap(Map<String, BigDecimal> map) {
        map.forEach((key, value) -> System.out.println(key + " - " + value));
    }
}