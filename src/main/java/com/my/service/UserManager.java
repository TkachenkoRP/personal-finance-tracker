package com.my.service;

import com.my.model.User;
import com.my.model.UserRole;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс UserManager для управления состоянием пользователя в системе.
 * Хранит информацию о текущем вошедшем пользователе и предоставляет методы для проверки его состояния.
 */
public class UserManager {
    @Getter
    @Setter
    private static User loggedInUser;

    private UserManager() {
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public static boolean isAdmin() {
        return isLoggedIn() && loggedInUser.getRole().equals(UserRole.ROLE_ADMIN);
    }

    public static boolean canWork(long id) {
        return isLoggedIn() && (loggedInUser.getId().equals(id) || isAdmin());
    }
}
