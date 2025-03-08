package com.my.in;

import com.my.out.ConsoleOutputHandler;

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

    public static long getUserLongInput(String request) {
        while (true) {
            ConsoleOutputHandler.displayMsg(request);
            if (scanner.hasNextLong()) {
                return scanner.nextLong();
            } else {
                ConsoleOutputHandler.displayMsg("Неверное значение!\n");
                scanner.next();
            }
        }
    }
}
