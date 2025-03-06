package com.my.out;

import com.my.model.User;

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
            System.out.println(currentUser.getEmail());
            System.out.println("0 - выход.");
        }
    }

    public static void displayMsg(String msg) {
        System.out.println(msg);
    }
}