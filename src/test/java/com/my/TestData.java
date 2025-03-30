package com.my;

import com.my.model.TransactionType;
import com.my.model.User;
import com.my.model.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestData {

    public static final String API_URL_REGISTER = "/api/auth/register";
    public static final String API_URL_LOGIN = "/api/auth/login";
    public static final String API_URL_LOGOUT = "/api/auth/logout";
    public static final String API_URL_USER = "/api/user";
    public static final String API_URL_CATEGORY = "/api/category";
    public static final String API_URL_TRANSACTION = "/api/transaction";
    public static final String API_URL_TRANSACTION_BALANCE = "/api/transaction/balance";
    public static final String API_URL_TRANSACTION_INCOME = "/api/transaction/income";
    public static final String API_URL_TRANSACTION_TOTAL_EXPENSES = "/api/transaction/total-expenses";
    public static final String API_URL_TRANSACTION_MONTH_EXPENSES = "/api/transaction/month-expenses";
    public static final String API_URL_TRANSACTION_ANALYZE = "/api/transaction/analyze";
    public static final String API_URL_TRANSACTION_REPORT = "/api/transaction/report";
    public static final String API_URL_BUDGET = "/api/budget";
    public static final String API_URL_GOAL = "/api/goal";

    public static final Long WRONG_ID = Long.MAX_VALUE;

    public static final Long ADMIN_ID = 1L;
    public static final String ADMIN_EMAIL = "admin@admin";
    public static final String ADMIN_PASSWORD = "aDmIn";
    public static final String ADMIN_NAME = "Admin";
    public static final UserRole ADMIN_ROLE = UserRole.ROLE_ADMIN;

    public static User getAdminRole() {
        User user = new User();
        user.setId(1L);
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
    public static final Long CATEGORY_ID_FOR_DELETE = 4L;

    public static final Long TRANSACTION_ID = 1L;
    public static final TransactionType TRANSACTION_TYPE = TransactionType.INCOME;
    public static final BigDecimal TRANSACTION_AMOUNT = new BigDecimal("300.00");
    public static final String TRANSACTION_DESCRIPTION = "Salary for January";
    public static final Long TRANSACTION_CATEGORY_ID = 1L;
    public static final Long TRANSACTION_USER_ID = 2L;

    public static final String NEW_TRANSACTION_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern("d.M.yyyy"));
    public static final String NEW_TRANSACTION_TYPE = TransactionType.INCOME.name();
    public static final BigDecimal NEW_TRANSACTION_AMOUNT = new BigDecimal("0.01");
    public static final String NEW_TRANSACTION_DESCRIPTION = "New description";
    public static final Long NEW_TRANSACTION_CATEGORY_ID = 1L;

    public static final Long TRANSACTION_ID_FOR_UPDATE = 5L;
    public static final TransactionType TRANSACTION_TYPE_FOR_UPDATE = TransactionType.EXPENSE;
    public static final String TRANSACTION_DATE_FOR_UPDATE = "10.1.2025";
    public static final BigDecimal TRANSACTION_AMOUNT_FOR_UPDATE = new BigDecimal("50.00");
    public static final String TRANSACTION_DESCRIPTION_FOR_UPDATE = "UtilitiesSalary for January";
    public static final Long TRANSACTION_USER_ID_FOR_UPDATE = 2L;

    public static final Long TRANSACTION_ID_6 = 6L;
    public static final TransactionType TRANSACTION_TYPE_6 = TransactionType.EXPENSE;
    public static final String TRANSACTION_DATE_6 = "15.1.2025";
    public static final BigDecimal TRANSACTION_AMOUNT_6 = new BigDecimal("30.00");
    public static final String TRANSACTION_DESCRIPTION_6 = "Transport";
    public static final Long TRANSACTION_CATEGORY_ID_6 = 3L;
    public static final Long TRANSACTION_USER_ID_6 = 2L;

    public static final Long TRANSACTION_ID_FOR_DELETE = 4L;

    public static final Long BUDGET_ID = 1L;
    public static final Long BUDGET_CATEGORY_ID = 1L;
    public static final BigDecimal BUDGET_AMOUNT = new BigDecimal("101.00");

    public static final BigDecimal NEW_BUDGET_AMOUNT = new BigDecimal("111.11");
    public static final String NEW_BUDGET_DATE_FROM = "1.5.2025";
    public static final String NEW_BUDGET_DATE_TO = "15.5.2025";
    public static final Long NEW_BUDGET_CATEGORY_ID = 1L;

    public static final Long BUDGET_ID_FOR_UPDATE = 2L;
    public static final Long BUDGET_CATEGORY_ID_FOR_UPDATE = 2L;
    public static final BigDecimal BUDGET_AMOUNT_FOR_UPDATE = new BigDecimal("201.00");

    public static final Long BUDGET_ID_FOR_DELETE = 3L;

    public static final Long GOAL_ID = 1L;
    public static final Long GOAL_CATEGORY_ID = 1L;
    public static final BigDecimal GOAL_AMOUNT = new BigDecimal("101.00");

    public static final BigDecimal NEW_GOAL_AMOUNT = new BigDecimal("111.11");
    public static final Long NEW_GOAL_CATEGORY_ID = 1L;

    public static final Long GOAL_ID_FOR_UPDATE = 2L;
    public static final Long GOAL_CATEGORY_ID_FOR_UPDATE = 2L;
    public static final BigDecimal GOAL_AMOUNT_FOR_UPDATE = new BigDecimal("55.55");

    public static final Long GOAL_ID_FOR_DELETE = 3L;
}
