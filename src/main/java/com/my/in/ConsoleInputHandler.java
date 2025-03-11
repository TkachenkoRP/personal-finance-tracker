package com.my.in;

import com.my.out.ConsoleOutputHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleInputHandler {

    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleInputHandler() {
    }

    public static String getUserTextInput(String request) {
        ConsoleOutputHandler.displayMsg(request);
        return scanner.next();
    }

    public static int getUserIntegerInput(String request) {
        while (true) {
            ConsoleOutputHandler.displayMsg(request);
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                ConsoleOutputHandler.displayMsg("Неверное значение!\n");
                scanner.next();
            }
        }
    }

    public static LocalDate getUserDateInput(String request) {
        int year;
        int month;
        int day;
        ConsoleOutputHandler.displayMsg(request);

        year = getUserIntegerInput("Введите год: ");

        do {
            month = getUserIntegerInput("Введите месяц (1-12): ");
        } while (month < 1 || month > 12);

        do {
            day = getUserIntegerInput("Введите день месяца (1-31): ");
        } while (day < 1 || day > 31);

        return LocalDate.of(year, month, day);
    }

    public static BigDecimal getUserBigDecimalInput(String request) {
        while (true) {
            ConsoleOutputHandler.displayMsg(request);
            String input = scanner.next();
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                ConsoleOutputHandler.displayMsg("Неверное значение! Пожалуйста, введите число.\n");
            }
        }
    }

    public static boolean getUserBooleanInput(String request) {
        while (true) {
            ConsoleOutputHandler.displayMsg(request);
            String input = scanner.next().trim().toLowerCase();
            if (input.equals("y") || input.equals("д")) {
                return true;
            } else if (input.equals("n") || input.equals("н")) {
                return false;
            } else {
                ConsoleOutputHandler.displayMsg("Неверный ввод! Пожалуйста, введите 'y'/'n' или 'д'/'н'.\n");
            }
        }
    }
}
