package com.my.app;

import com.my.in.ConsoleInputHandler;
import com.my.model.User;
import com.my.out.ConsoleOutputHandler;
import com.my.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;

public class ConsoleApp {
    private User currentUser = null;
    private final UserService userService;

    public ConsoleApp(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        boolean working = true;

        while (working) {
            ConsoleOutputHandler.displayMenu(currentUser);
            int choice = ConsoleInputHandler.getUserIntegerInput("Ваш выбор: ");
            working = handleUserChoice(choice);
        }
    }

    private boolean handleUserChoice(int choice) {
        if (currentUser == null && new ArrayList<>(Arrays.asList(3, 4, 5, 6)).contains(choice)) {
            choice = -1;
        }
        if (currentUser != null && new ArrayList<>(Arrays.asList(1, 2)).contains(choice)) {
            choice = -1;
        }
        switch (choice) {
            case 1 -> registrationUser();
            case 2 -> loginUser();
            case 3 -> editCurrentUserData();
            case 4 -> editUserData();
            case 5 -> deleteCurrentUser();
            case 6 -> deleteUser();
            case 0 -> {
                return exit();
            }
            default -> ConsoleOutputHandler.displayMsg("Неверный выбор!\n");
        }
        return true;
    }

    private void registrationUser() {
        ConsoleOutputHandler.displayMsg("\nРегистрация пользователя");
        String userEmail = ConsoleInputHandler.getUserTextInput("Введите email пользователя: ");
        String userName = ConsoleInputHandler.getUserTextInput("Введите имя пользователя: ");
        String password = ConsoleInputHandler.getUserTextInput("Введите пароль: ");
        currentUser = userService.registration(userEmail, userName, password);
        String msg = currentUser != null ?
                "\nРегистрация " + currentUser.getEmail() + " прошла успешно!\n" :
                "\nРегистрация не удалась\n";
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void loginUser() {
        ConsoleOutputHandler.displayMsg("\nВход");
        String userEmail = ConsoleInputHandler.getUserTextInput("Введите email пользователя: ");
        String password = ConsoleInputHandler.getUserTextInput("Введите пароль: ");
        currentUser = userService.login(userEmail, password);
        String msg = currentUser != null ?
                "\nУспешный вход!\n" :
                "\nНеудачный вход!\n";
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void editCurrentUserData() {
        ConsoleOutputHandler.displayMsg("\nРедактирование пользователя " + currentUser.getName());
        editUserData(currentUser);
    }

    private void editUserData() {
        ConsoleOutputHandler.displayMsg("\nРедактирование пользователей");
        displayAllUsers();
        Long userId = ConsoleInputHandler.getUserLongInput("Введите id пользователя для редактирования: ");
        User user = userService.getById(userId);
        if (user == null) {
            ConsoleOutputHandler.displayMsg("Ошибка: пользователь не найден.");
            return;
        }
        editUserData(user);
    }

    private void editUserData(User user) {
        User userForUpdate = getUserDataForUpdate(user);
        User updated = userService.update(user.getId(), userForUpdate);
        if (updated != null) {
            ConsoleOutputHandler.displayMsg("Данные пользователя успешно обновлены.");
        } else {
            ConsoleOutputHandler.displayMsg("Ошибка: email уже занят.");
        }
    }

    private User getUserDataForUpdate(User user) {
        String userEmail = ConsoleInputHandler.getUserTextInput("Измените email " + user.getEmail() + " на: ");
        String userName = ConsoleInputHandler.getUserTextInput("Измените имя " + user.getName() + " на: ");
        String password = ConsoleInputHandler.getUserTextInput("Введите новый пароль: ");
        return new User(userEmail, password, userName);
    }

    private void displayAllUsers() {
        ConsoleOutputHandler.displayMsg("Список пользователей");
        ConsoleOutputHandler.displayUserList(userService.getAll());
    }

    private boolean exit() {
        if (currentUser != null) {
            currentUser = null;
            return true;
        }
        return false;
    }

    private void deleteCurrentUser() {
        if (userService.delete(currentUser.getId())) {
            currentUser = null;
        }
    }

    private void deleteUser() {
        displayAllUsers();
        Long userId = ConsoleInputHandler.getUserLongInput("Введите id пользователя для удаления: ");
        if (userService.delete(userId)) {
            ConsoleOutputHandler.displayMsg("Пользователь удален.");
        } else {
            ConsoleOutputHandler.displayMsg("Ошибка: пользователь не найден.");
        }
    }
}
