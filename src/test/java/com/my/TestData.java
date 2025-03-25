package com.my;

import com.my.model.UserRole;

public class TestData {
    public static final Long ADMIN_ID = 1L;
    public static final String ADMIN_EMAIL = "admin@admin";
    public static final String ADMIN_PASSWORD = "aDmIn";
    public static final String ADMIN_NAME = "Admin";
    public static final UserRole ADMIN_ROLE = UserRole.ROLE_ADMIN;

    public static final Long USER_ID = 2L;
    public static final String USER_EMAIL = "user1@example.com";
    public static final String USER_PASSWORD = "password1";
    public static final String USER_NAME = "User One";
    public static final UserRole USER_ROLE = UserRole.ROLE_USER;

    public static final String NEW_USER_EMAIL = "new@new";
    public static final String NEW_USER_PASSWORD = "newPassword";
    public static final String NEW_USER_NAME = "New Name";
}
