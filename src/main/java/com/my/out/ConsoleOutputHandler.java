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
        StringBuilder menu = new StringBuilder("Доступные команды:\n");

        if (currentUser == null) {
            menu.append("""
                    1 - регистрация пользователя;
                    2 - авторизация пользователя;
                    0 - завершить работу.
                    """);
        } else {
            menu.append(currentUser.getName()).append("\n")
                    .append("""
                            3 - редактировать профиль.
                            4 - удалить текущего пользователя.
                            5 - отобразить все категории.
                            6 - добавить категорию.
                            7 - редактировать категорию.
                            8 - отобразить все транзакции.
                            9 - добавить транзакцию.
                            10 - редактировать транзакцию.
                            11 - удалить транзакцию.
                            12 - отобразить бюджет.
                            13 - отредактировать бюджет.
                            14 - отобразить цель.
                            15 - отредактировать цель.
                            16 - отобразить баланс.
                            17 - отобразить баланс в периоде.
                            18 - отобразить доход в периоде.
                            19 - отобразить расход в периоде.
                            20 - отобразить расход по категориям.
                            21 - отобразить финансовый отчет.
                            0 - выход.
                            """);
        }

        if (currentUser != null && currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            menu.append("""
                    \nПанель управления для администратора:
                    22 - отобразить всех пользователей.
                    23 - редактировать пользователя.
                    24 - удалить пользователя.
                    25 - удалить категорию.
                    26 - заблокировать пользователя.
                    """);
        }

        System.out.println(menu);
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