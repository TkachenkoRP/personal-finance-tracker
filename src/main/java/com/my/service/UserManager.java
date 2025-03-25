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

    /**
     * Проверяет, является ли пользователь вошедшим в систему.
     *
     * @return true, если пользователь вошел в систему, иначе false
     */
    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    /**
     * Проверяет, является ли текущий вошедший пользователь администратором.
     *
     * @return true, если пользователь вошел в систему и имеет роль администратора, иначе false
     */
    public static boolean isAdmin() {
        return isLoggedIn() && loggedInUser.getRole().equals(UserRole.ROLE_ADMIN);
    }
}
