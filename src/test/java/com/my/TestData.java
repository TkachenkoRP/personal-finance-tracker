package com.my;

import com.my.model.User;
import com.my.model.UserRole;

public class TestData {

    public static final Long WRONG_ID = Long.MAX_VALUE;

    public static final Long ADMIN_ID = 1L;
    public static final String ADMIN_EMAIL = "admin@admin";
    public static final String ADMIN_PASSWORD = "aDmIn";
    public static final String ADMIN_NAME = "Admin";
    public static final UserRole ADMIN_ROLE = UserRole.ROLE_ADMIN;

    public static User getAdminRole() {
        User user = new User();
        user.setRole(UserRole.ROLE_ADMIN);
        return user;
    }

    public static final Long USER_ID = 2L;
    public static final String USER_EMAIL = "user1@example.com";
    public static final String USER_PASSWORD = "password1";
    public static final String USER_NAME = "User One";
    public static final UserRole USER_ROLE = UserRole.ROLE_USER;

    public static User getUserRole(Long id) {
        User user = new User();
        user.setRole(UserRole.ROLE_USER);
        user.setId(id);
        return user;
    }

    public static final String NEW_USER_EMAIL = "new@new";
    public static final String NEW_USER_PASSWORD = "newPassword";
    public static final String NEW_USER_NAME = "New Name";
    public static final Long USER_ID_FOR_UPDATE = 3L;
    public static final Long USER_ID_FOR_DELETE = 4L;
    public static final Long USER_ID_FOR_UNBLOCK = 5L;

    public static final Long CATEGORY_ID = 1L;
    public static final String CATEGORY_NAME = "Salary";
    public static final String NEW_CATEGORY_NAME = "New Category";
    public static final Long CATEGORY_ID_FOR_UPDATE = 2L;
    public static final String CATEGORY_NAME_FOR_UPDATE = "Freelance";
    public static final Long CATEGORY_ID_FOR_DELETE = 4L;

}
