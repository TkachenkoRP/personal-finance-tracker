package com.my.app;

import com.my.in.ConsoleInputHandler;
import com.my.model.Transaction;
import com.my.model.TransactionCategory;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.out.ConsoleOutputHandler;
import com.my.service.JdbcDataService;
import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;
import com.my.service.impl.JdbcDataServiceImpl;
import liquibase.exception.LiquibaseException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConsoleApp {
    private User currentUser = null;
    private final UserService userService;
    private final TransactionService transactionService;
    private final TransactionCategoryService transactionCategoryService;
    private final NotificationService notificationService;
    private final NotificationService emailNotificationService;
    private static final Set<Integer> UNAUTHENTICATED_CHOICES = Set.of(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22);
    private static final Set<Integer> AUTHENTICATED_CHOICES = Set.of(1, 2);
    private static final Set<Integer> ADMIN_CHOICES = Set.of(22, 23, 24, 25, 26);

    public ConsoleApp(UserService userService, TransactionService transactionService,
                      TransactionCategoryService transactionCategoryService, NotificationService notificationService,
                      NotificationService emailNotificationService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.transactionCategoryService = transactionCategoryService;
        this.notificationService = notificationService;
        this.emailNotificationService = emailNotificationService;
    }

    public void start() {
        init();
        boolean working = true;
        while (working) {
            ConsoleOutputHandler.displayMenu(currentUser);
            int choice = ConsoleInputHandler.getUserIntegerInput("Ваш выбор: ");
            working = handleUserChoice(choice);
        }
    }

    private void init() {
        try {
            JdbcDataService jdbcDataService = new JdbcDataServiceImpl();
            jdbcDataService.initDb();
        } catch (LiquibaseException e) {
            ConsoleOutputHandler.displayMsg("Ошибка инициализации БД!");
            System.err.println(e.getMessage());
        }
    }

    private boolean handleUserChoice(int choice) {
        if (currentUser == null && UNAUTHENTICATED_CHOICES.contains(choice)) {
            choice = -1;
        }
        if (currentUser != null && AUTHENTICATED_CHOICES.contains(choice)) {
            choice = -1;
        }
        if (currentUser != null && !currentUser.getRole().equals(UserRole.ROLE_ADMIN) && ADMIN_CHOICES.contains(choice)) {
            choice = -1;
        }
        switch (choice) {
            case 1 -> registrationUser();
            case 2 -> loginUser();

            case 3 -> editCurrentUserData();
            case 4 -> deleteCurrentUser();
            case 5 -> displayAllCategories();
            case 6 -> addCategory();
            case 7 -> editCategory();
            case 8 -> displayAllTransactions();
            case 9 -> addTransaction();
            case 10 -> editTransaction();
            case 11 -> deleteTransaction();
            case 12 -> displayBudget();
            case 13 -> editBudget();
            case 14 -> displayGoal();
            case 15 -> editGoal();
            case 16 -> displayBalance();
            case 17 -> displayBalanceByDate();
            case 18 -> displayTotalIncome();
            case 19 -> displayTotalExpenses();
            case 20 -> displayAnalyzeExpensesByCategory();
            case 21 -> displayFinancialReport();

            case 22 -> displayAllUsers();
            case 23 -> editUserData();
            case 24 -> deleteUser();
            case 25 -> deleteCategory();
            case 26 -> blockUser();

            case 0 -> {
                return exit();
            }
            default -> ConsoleOutputHandler.displayMsg("Неверный выбор!\n");
        }
        return true;
    }

    private void blockUser() {
        displayAllUsers();
        long userId = ConsoleInputHandler.getUserIntegerInput("Введите id пользователя для блокировки: ");
        try {
            if (userService.blockUser(userId)) {
                ConsoleOutputHandler.displayMsg("Пользователь заблокирован.");
            } else {
                ConsoleOutputHandler.displayMsg("Ошибка: пользователь не найден.");
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса блокировки пользователя - " + userId);
            System.err.println(e.getMessage());
        }
    }

    private void displayFinancialReport() {
        LocalDate[] dates = getInputDates();
        try {
            Map<String, BigDecimal> report = transactionService.generateFinancialReport(currentUser.getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMap(report);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса финансового отчета.");
            System.err.println(e.getMessage());
        }
    }

    private void displayAnalyzeExpensesByCategory() {
        LocalDate[] dates = getInputDates();
        try {
            Map<String, BigDecimal> analyzed = transactionService.analyzeExpensesByCategory(currentUser.getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMap(analyzed);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса анализа по категориям.");
            System.err.println(e.getMessage());
        }

    }

    private void displayTotalExpenses() {
        LocalDate[] dates = getInputDates();
        try {
            BigDecimal expenses = transactionService.getTotalExpenses(currentUser.getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMsg("Расходы с " + dates[0] + " до " + dates[1] + ": " + expenses);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса расходов.");
            System.err.println(e.getMessage());
        }
    }

    private void displayTotalIncome() {
        LocalDate[] dates = getInputDates();
        try {
            BigDecimal income = transactionService.getTotalIncome(currentUser.getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMsg("Доход с " + dates[0] + " до " + dates[1] + ": " + income);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса доходов.");
            System.err.println(e.getMessage());
        }
    }

    private void displayBalanceByDate() {
        LocalDate[] dates = getInputDates();

        try {
            BigDecimal income = transactionService.getTotalIncome(currentUser.getId(), dates[0], dates[1]);
            BigDecimal expenses = transactionService.getTotalExpenses(currentUser.getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMsg("Баланс c " + dates[0] + " по " + dates[1] + ": " + income.subtract(expenses));
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса баланса.");
            System.err.println(e.getMessage());
        }
    }

    private LocalDate[] getInputDates() {
        LocalDate from = ConsoleInputHandler.getUserDateInput("Введите дату начала периода");
        LocalDate to = ConsoleInputHandler.getUserDateInput("Введите дату конца периода");
        return new LocalDate[]{from, to};
    }

    private void displayBalance() {
        try {
            BigDecimal balance = transactionService.getBalance(currentUser.getId());
            ConsoleOutputHandler.displayMsg("Баланс: " + balance);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса баланса.");
            System.err.println(e.getMessage());
        }
    }

    private void editGoal() {
        ConsoleOutputHandler.displayMsg("\nРедактирование цели накопления");
        User updated = null;
        BigDecimal goalAmount = null;
        try {
            Map<Long, BigDecimal> goals = currentUser.getGoals();
            List<TransactionCategory> transactionCategories = transactionCategoryService.getAll();
            if (!goals.isEmpty()) {
                ConsoleOutputHandler.displayMapWithCategories(goals, transactionCategories);
            } else {
                ConsoleOutputHandler.displayTransactionCategoryList(transactionCategories);
            }
            long goalId = ConsoleInputHandler.getUserIntegerInput("Выберите цель: ");
            goalAmount = ConsoleInputHandler.getUserBigDecimalInput("Какова новая цель?");
            currentUser.getGoals().put(goalId, goalAmount);
            updated = userService.update(currentUser.getId(), currentUser);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса установки цели.");
            System.err.println(e.getMessage());
        }
        if (updated != null) {
            ConsoleOutputHandler.displayMsg("Цель успешно установлена на " + goalAmount);
        } else {
            ConsoleOutputHandler.displayMsg("Не удалось установить цель.");
        }
    }

    private void displayGoal() {
        if (!currentUser.getGoals().isEmpty()) {
            try {
                List<TransactionCategory> transactionCategories = transactionCategoryService.getAll();
                ConsoleOutputHandler.displayMapWithCategories(currentUser.getGoals(), transactionCategories);
            } catch (SQLException e) {
                ConsoleOutputHandler.displayMsg("Ошибка запроса отображения целей.");
                System.err.println(e.getMessage());
            }
        } else {
            ConsoleOutputHandler.displayMsg("Цели не установлены.");
        }
    }

    private void editBudget() {
        ConsoleOutputHandler.displayMsg("\nРедактирование бюджета");
        String msg = currentUser.getBudget() == null ? "Установить бюджет: " : "Бюджет - " + currentUser.getBudget() + ", изменить на: ";
        BigDecimal budget = ConsoleInputHandler.getUserBigDecimalInput(msg);
        currentUser.setBudget(budget);
        User updated = null;
        try {
            updated = userService.update(currentUser.getId(), currentUser);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса установки бюджета.");
            System.err.println(e.getMessage());
        }
        if (updated != null) {
            ConsoleOutputHandler.displayMsg("Бюджет успешно установлен на " + budget);
        } else {
            ConsoleOutputHandler.displayMsg("Не удалось обновить бюджет.");
        }
    }

    private void displayBudget() {
        BigDecimal budget = currentUser.getBudget();
        String msg = budget == null ? "Бюджет не установлен" : "Ваш бюджет: " + budget;
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void deleteTransaction() {
        ConsoleOutputHandler.displayMsg("\nУдаление транзакции");
        displayTransactions(new TransactionFilter(currentUser.getId()));
        long transactionId = ConsoleInputHandler.getUserIntegerInput("Введите id транзакции: ");
        try {
            if (transactionService.deleteById(transactionId)) {
                ConsoleOutputHandler.displayMsg("Транзакция удалена!");
            } else {
                ConsoleOutputHandler.displayMsg("Удаление не удалось!");
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса удаления транзакции.");
            System.err.println(e.getMessage());
        }
    }

    private void editTransaction() {
        ConsoleOutputHandler.displayMsg("\nРедактирование транзакции");
        displayTransactions(new TransactionFilter(currentUser.getId()));
        long transactionId = ConsoleInputHandler.getUserIntegerInput("Введите id транзакции: ");
        try {
            Transaction transaction = transactionService.getById(transactionId);
            if (transaction == null) {
                ConsoleOutputHandler.displayMsg("Ошибка: транзакция не найдена.");
                return;
            }
            BigDecimal amount = ConsoleInputHandler.getUserBigDecimalInput("Сумма - " + transaction.getAmount() + ", изменить на: ");
            String desc = ConsoleInputHandler.getUserTextInput("Описание - " + transaction.getDescription() + ", изменить на: ");
            displayAllCategories();
            long categoryId = ConsoleInputHandler.getUserIntegerInput("Выберите категорию: ");
            TransactionCategory transactionCategory = transactionCategoryService.getById(categoryId);
            if (transactionCategory == null) {
                ConsoleOutputHandler.displayMsg("Ошибка: категория не найдена.");
                return;
            }
            Transaction request = new Transaction();
            request.setAmount(amount);
            request.setDescription(desc);
            request.setCategory(transactionCategory);
            transactionService.update(transactionId, request);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса редактирования транзакции.");
            System.err.println(e.getMessage());
        }
    }

    private void addTransaction() {
        ConsoleOutputHandler.displayMsg("\nСоздание транзакции");
        LocalDate date = ConsoleInputHandler.getUserDateInput("Введите дату");
        ConsoleOutputHandler.displayEnum(TransactionType.class);
        int transactionType = ConsoleInputHandler.getUserIntegerInput("Выберите тип транзакции: ");
        BigDecimal amount = ConsoleInputHandler.getUserBigDecimalInput("Введите сумму: ");
        String desc = ConsoleInputHandler.getUserTextInput("Введите описание транзакции: ");
        displayAllCategories();
        long categoryId = ConsoleInputHandler.getUserIntegerInput("Введите id категории: ");
        try {
            TransactionCategory transactionCategory = transactionCategoryService.getById(categoryId);

            Transaction transaction =
                    new Transaction(date, TransactionType.values()[transactionType], amount, desc, transactionCategory);
            transaction.setUser(currentUser);

            transactionService.save(transaction);
            if (transaction.getType() == TransactionType.EXPENSE) {
                checkBudgetExceeded();
            } else {
                checkGoalIncome(transactionCategory.getId());
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса добавления транзакции.");
            System.err.println(e.getMessage());
        }
    }

    private void checkGoalIncome(Long transactionCategoryId) {
        try {
            if (currentUser.getGoals() != null && currentUser.getGoals().get(transactionCategoryId) != null && transactionService.isGoalIncome(currentUser.getId(), currentUser.getGoals().get(transactionCategoryId), transactionCategoryId)) {
                TransactionCategory transactionCategory = transactionCategoryService.getById(transactionCategoryId);
                String msg = "Выполнена цель " + currentUser.getGoals().get(transactionCategoryId) + " для " + transactionCategory.getCategoryName();
                ConsoleOutputHandler.displayMsg(msg);
                notificationService.sendNotification(msg);
                emailNotificationService.sendNotification(msg);
                currentUser.getGoals().remove(transactionCategoryId);
                userService.update(currentUser.getId(), currentUser);
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса проверки цели.");
            System.err.println(e.getMessage());
        }
    }

    private void checkBudgetExceeded() {
        try {
            if (transactionService.isBudgetExceeded(currentUser.getId(), currentUser.getBudget())) {
                BigDecimal monthExpense = transactionService.getMonthExpense(currentUser.getId());
                String msg = "Перерасход бюджета! Ваши расходы: " + monthExpense +
                        ", установленный бюджет: " + currentUser.getBudget();
                ConsoleOutputHandler.displayMsg(msg);
                notificationService.sendNotification(msg);
                emailNotificationService.sendNotification(msg);
                currentUser.setBudget(null);

                userService.update(currentUser.getId(), currentUser);
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса проверки бюджета.");
            System.err.println(e.getMessage());
        }
    }

    private void displayAllTransactions() {
        ConsoleOutputHandler.displayMsg("\nЗадайте параметры фильтра: ");

        boolean useCategory = ConsoleInputHandler.getUserBooleanInput("Использовать фильтр по категории? ('y'/'n' или 'д'/'н'.)");
        Long transactionCategoryId = null;
        if (useCategory) {
            displayAllCategories();
            transactionCategoryId = (long) ConsoleInputHandler.getUserIntegerInput("Введите id категории (или 0 для игнорирования): ");
        }

        boolean useDate = ConsoleInputHandler.getUserBooleanInput("Использовать фильтр по дате? ('y'/'n' или 'д'/'н'.)");
        LocalDate date = null;
        if (useDate) {
            date = ConsoleInputHandler.getUserDateInput("Введите дату");
        }

        boolean useType = ConsoleInputHandler.getUserBooleanInput("Использовать фильтр по типу транзакции? ('y'/'n' или 'д'/'н'.)");
        TransactionType transactionType = null;
        if (useType) {
            ConsoleOutputHandler.displayEnum(TransactionType.class);
            int transactionTypeOrdinal = ConsoleInputHandler.getUserIntegerInput("Введите id типа: ");
            transactionType = TransactionType.values()[transactionTypeOrdinal];
        }

        long userId = currentUser.getId();

        if (isAdmin()) {
            int choice;
            do {
                choice = ConsoleInputHandler.getUserIntegerInput("1 - Свои транзакции, 2 - Транзакции другого пользователя");
            } while (choice < 1 || choice > 2);
            if (choice == 2) {
                displayAllUsers();
                userId = ConsoleInputHandler.getUserIntegerInput("Введите id пользователя: ");
            }
        }

        TransactionFilter filter = new TransactionFilter(userId, date, null, null, transactionCategoryId, transactionType);
        displayTransactions(filter);
    }

    private void displayTransactions(TransactionFilter filter) {
        ConsoleOutputHandler.displayMsg("\nСписок транзакций");
        try {
            List<Transaction> transactions = transactionService.getAll(filter);
            ConsoleOutputHandler.displayTransactionList(transactions);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса транзакций.");
            System.err.println(e.getMessage());
        }
    }


    private void registrationUser() {
        ConsoleOutputHandler.displayMsg("\nРегистрация пользователя");
        String userEmail = ConsoleInputHandler.getUserTextInput("Введите email пользователя: ");
        String userName = ConsoleInputHandler.getUserTextInput("Введите имя пользователя: ");
        String password = ConsoleInputHandler.getUserTextInput("Введите пароль: ");
        try {
            currentUser = userService.registration(userEmail, userName, password);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса регистрации пользователя.");
            System.err.println(e.getMessage());
        }
        String msg = currentUser != null ?
                "\nРегистрация " + currentUser.getEmail() + " прошла успешно!\n" :
                "\nРегистрация не удалась\n";
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void loginUser() {
        ConsoleOutputHandler.displayMsg("\nВход");
        String userEmail = ConsoleInputHandler.getUserTextInput("Введите email пользователя: ");
        String password = ConsoleInputHandler.getUserTextInput("Введите пароль: ");
        try {
            currentUser = userService.login(userEmail, password);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса авторизации пользователя.");
            System.err.println(e.getMessage());
        }
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
        long userId = ConsoleInputHandler.getUserIntegerInput("Введите id пользователя для редактирования: ");
        User user = null;
        try {
            user = userService.getById(userId);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса редактирования данных пользователя.");
            System.err.println(e.getMessage());
        }
        if (user == null) {
            ConsoleOutputHandler.displayMsg("Ошибка: пользователь не найден.");
            return;
        }
        editUserData(user);
    }

    private void editUserData(User user) {
        User userForUpdate = getUserDataForUpdate(user);
        User updated = null;
        try {
            updated = userService.update(user.getId(), userForUpdate);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса редактирования данных пользователя.");
            System.err.println(e.getMessage());
        }
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
        return new User(userEmail, password, userName, user.getRole());
    }

    private void displayAllUsers() {
        ConsoleOutputHandler.displayMsg("Список пользователей");
        try {
            ConsoleOutputHandler.displayUserList(userService.getAll());
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса выборки всех пользователей.");
            System.err.println(e.getMessage());
        }
    }

    private boolean exit() {
        if (currentUser != null) {
            currentUser = null;
            return true;
        }
        return false;
    }

    private void deleteCurrentUser() {
        try {
            if (userService.delete(currentUser.getId())) {
                currentUser = null;
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса удаления текущего пользователя.");
        }
    }

    private void deleteUser() {
        displayAllUsers();
        long userId = ConsoleInputHandler.getUserIntegerInput("Введите id пользователя для удаления: ");
        try {
            if (userService.delete(userId)) {
                ConsoleOutputHandler.displayMsg("Пользователь удален.");
            } else {
                ConsoleOutputHandler.displayMsg("Ошибка: пользователь не найден.");
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса удаления пользователя - " + userId);
            System.err.println(e.getMessage());
        }
    }

    private void deleteCategory() {
        displayAllCategories();
        long transactionCategoryId = ConsoleInputHandler.getUserIntegerInput("Введите id категории для удаления: ");
        try {
            if (transactionCategoryService.deleteById(transactionCategoryId)) {
                ConsoleOutputHandler.displayMsg("Категория удалена!");
            } else {
                ConsoleOutputHandler.displayMsg("\nУдаление не удалось!\n");
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса удаления категории - " + transactionCategoryId);
            System.err.println(e.getMessage());
        }
    }

    private void editCategory() {
        ConsoleOutputHandler.displayMsg("\nРедактирование категории");
        displayAllCategories();
        long transactionCategoryId = ConsoleInputHandler.getUserIntegerInput("Введите id категории для редактирования: ");
        String transactionCategoryName = ConsoleInputHandler.getUserTextInput("Введите название категории: ");
        TransactionCategory updated = null;
        try {
            updated = transactionCategoryService.update(transactionCategoryId, new TransactionCategory(transactionCategoryName));
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса редактирования категории.");
            System.err.println(e.getMessage());
        }
        if (updated == null) {
            ConsoleOutputHandler.displayMsg("Ошибка: категория не найдена.");
        } else {
            ConsoleOutputHandler.displayMsg("Категория отредактирована.");
        }
    }

    private void addCategory() {
        ConsoleOutputHandler.displayMsg("\nДобавление категории");
        String transactionCategoryName = ConsoleInputHandler.getUserTextInput("Введите название категории: ");
        TransactionCategory transactionCategoryRequest = new TransactionCategory(transactionCategoryName);
        TransactionCategory transactionCategory = null;
        try {
            transactionCategory = transactionCategoryService.save(transactionCategoryRequest);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса добавления категории.");
            System.err.println(e.getMessage());
        }
        String msg = transactionCategory != null ?
                "\nДобавлен новый тип: " + transactionCategory.getCategoryName() + "\n" :
                "\nДобавление не удалось\n";
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void displayAllCategories() {
        ConsoleOutputHandler.displayMsg("\nСписок категорий");

        try {
            List<TransactionCategory> transactionCategories = transactionCategoryService.getAll();
            ConsoleOutputHandler.displayTransactionCategoryList(transactionCategories);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса всех категорий.");
            System.err.println(e.getMessage());
        }
    }

    private boolean isAdmin() {
        return currentUser.getRole() == UserRole.ROLE_ADMIN;
    }
}
