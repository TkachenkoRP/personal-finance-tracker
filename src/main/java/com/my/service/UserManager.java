package com.my.service;

import com.my.model.User;
import com.my.model.UserRole;
import lombok.Getter;
import lombok.Setter;

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
}
