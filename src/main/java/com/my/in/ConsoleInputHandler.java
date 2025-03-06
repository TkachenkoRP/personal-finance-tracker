package com.my.in;

import com.my.out.ConsoleOutputHandler;

import java.util.Scanner;

public class ConsoleInputHandler {

    private ConsoleInputHandler() {
    }

    public static String getUserTextInput(String request) {
        Scanner scanner = new Scanner(System.in);
        ConsoleOutputHandler.displayMsg(request);
        return scanner.next();
    }

    public static int getUserIntegerInput(String request) {
        Scanner scanner = new Scanner(System.in);
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
}
