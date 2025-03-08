package com.my.out;

import com.my.model.User;

import java.util.List;

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
            System.out.println("4 - редактировать пользователя.");
            System.out.println("5 - удалить текущего пользователя.");
            System.out.println("6 - удалить пользователя.");
            System.out.println("0 - выход.");
        }
    }

    public static void displayMsg(String msg) {
        System.out.println(msg);
    }

    public static void displayUserList(List<User> users) {
        users.forEach(u -> System.out.println(u.getId() + " - " + u.getEmail() + " - " + u.getName()));
    }
}