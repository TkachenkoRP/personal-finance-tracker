package com.my.app;

import com.my.in.ConsoleInputHandler;
import com.my.model.Transaction;
import com.my.model.TransactionCategory;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.model.User;
import com.my.out.ConsoleOutputHandler;
import com.my.service.NotificationService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;

import java.math.BigDecimal;
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
    private static final Set<Integer> UNAUTHENTICATED_CHOICES = Set.of(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
    private static final Set<Integer> AUTHENTICATED_CHOICES = Set.of(1, 2);

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
        boolean working = true;
        addTestData();
        while (working) {
            ConsoleOutputHandler.displayMenu(currentUser);
            int choice = ConsoleInputHandler.getUserIntegerInput("Ваш выбор: ");
            working = handleUserChoice(choice);
        }
    }

    private void addTestData() {
        User registration = userService.registration("qwe", "rty", "qwe");
        User registration1 = userService.registration("asd", "fgh", "asd");

        TransactionCategory transactionCategory = new TransactionCategory("cat1");
        TransactionCategory transactionCategory2 = new TransactionCategory("cat2");

        TransactionCategory saved = transactionCategoryService.save(transactionCategory);
        TransactionCategory saved2 = transactionCategoryService.save(transactionCategory2);

        Transaction transaction1 = new Transaction(LocalDate.now().minusDays(1), TransactionType.INCOME, new BigDecimal("100.01"), "Descr", saved);
        transaction1.setUser(registration);
        Transaction transaction2 = new Transaction(LocalDate.now(), TransactionType.EXPENSE, new BigDecimal("50.55"), "Descr2", saved);
        transaction2.setUser(registration);
        Transaction transaction3 = new Transaction(LocalDate.now(), TransactionType.INCOME, new BigDecimal("10.22"), "Descr3", saved2);
        transaction3.setUser(registration);
        Transaction transaction4 = new Transaction(LocalDate.now(), TransactionType.INCOME, new BigDecimal("20.11"), "Descr4", saved2);
        transaction4.setUser(registration);
        Transaction transaction5 = new Transaction(LocalDate.now(), TransactionType.EXPENSE, new BigDecimal("14.55"), "Descr5", saved);
        transaction5.setUser(registration1);

        transactionService.save(transaction1);
        transactionService.save(transaction2);
        transactionService.save(transaction3);
        transactionService.save(transaction4);
        transactionService.save(transaction5);
    }

    private boolean handleUserChoice(int choice) {
        if (currentUser == null && UNAUTHENTICATED_CHOICES.contains(choice)) {
            choice = -1;
        }
        if (currentUser != null && AUTHENTICATED_CHOICES.contains(choice)) {
            choice = -1;
        }
        switch (choice) {
            case 1 -> registrationUser();
            case 2 -> loginUser();
            case 3 -> editCurrentUserData();
            case 4 -> editUserData();
            case 5 -> deleteCurrentUser();
            case 6 -> deleteUser();
            case 7 -> displayAllUsers();
            case 8 -> displayAllTransactions();
            case 9 -> addTransaction();
            case 10 -> editTransaction();
            case 11 -> deleteTransaction();
            case 12 -> displayAllCategories();
            case 13 -> addCategory();
            case 14 -> editCategory();
            case 15 -> deleteCategory();
            case 16 -> displayBudget();
            case 17 -> editBudget();
            case 18 -> displayGoal();
            case 19 -> editGoal();
            case 20 -> displayBalance();
            case 21 -> displayBalanceByDate();
            case 22 -> displayTotalIncome();
            case 23 -> displayTotalExpenses();
            case 24 -> displayAnalyzeExpensesByCategory();
            case 25 -> displayFinancialReport();
            case 0 -> {
                return exit();
            }
            default -> ConsoleOutputHandler.displayMsg("Неверный выбор!\n");
        }
        return true;
    }

    private void displayFinancialReport() {
        LocalDate from = ConsoleInputHandler.getUserDateInput("Введите дату начала периода");
        LocalDate to = ConsoleInputHandler.getUserDateInput("Введите дату конца периода");
        Map<String, BigDecimal> report = transactionService.generateFinancialReport(currentUser.getId(), from, to);
        ConsoleOutputHandler.displayMap(report);
    }

    private void displayAnalyzeExpensesByCategory() {
        LocalDate from = ConsoleInputHandler.getUserDateInput("Введите дату начала периода");
        LocalDate to = ConsoleInputHandler.getUserDateInput("Введите дату конца периода");
        Map<String, BigDecimal> analyzed = transactionService.analyzeExpensesByCategory(currentUser.getId(), from, to);
        ConsoleOutputHandler.displayMap(analyzed);
    }

    private void displayTotalExpenses() {
        LocalDate from = ConsoleInputHandler.getUserDateInput("Введите дату начала периода");
        LocalDate to = ConsoleInputHandler.getUserDateInput("Введите дату конца периода");
        BigDecimal expenses = transactionService.calculateTotalExpenses(currentUser.getId(), from, to);
        ConsoleOutputHandler.displayMsg("Расходы с " + from + " до " + to + ": " + expenses);
    }

    private void displayTotalIncome() {
        LocalDate from = ConsoleInputHandler.getUserDateInput("Введите дату начала периода");
        LocalDate to = ConsoleInputHandler.getUserDateInput("Введите дату конца периода");
        BigDecimal income = transactionService.calculateTotalIncome(currentUser.getId(), from, to);
        ConsoleOutputHandler.displayMsg("Доход с " + from + " до " + to + ": " + income);
    }

    private void displayBalanceByDate() {
        LocalDate from = ConsoleInputHandler.getUserDateInput("Введите дату начала периода");
        LocalDate to = ConsoleInputHandler.getUserDateInput("Введите дату конца периода");

        BigDecimal income = transactionService.calculateTotalIncome(currentUser.getId(), from, to);
        BigDecimal expenses = transactionService.calculateTotalExpenses(currentUser.getId(), from, to);

        ConsoleOutputHandler.displayMsg("Баланс c " + from + " по " + to + ": " + income.subtract(expenses));
    }

    private void displayBalance() {
        BigDecimal balance = transactionService.checkBalance(currentUser.getId());
        ConsoleOutputHandler.displayMsg("Баланс: " + balance);
    }

    private void editGoal() {
        ConsoleOutputHandler.displayMsg("\nРедактирование цели накопления");
        Map<Long, BigDecimal> goals = currentUser.getGoals();
        List<TransactionCategory> transactionCategories = transactionCategoryService.getAll();

        if (!goals.isEmpty()) {
            ConsoleOutputHandler.displayMapWithCategories(goals, transactionCategories);
        } else {
            ConsoleOutputHandler.displayTransactionCategoryList(transactionCategories);
        }
        long goalId = ConsoleInputHandler.getUserIntegerInput("Выберите цель: ");
        BigDecimal goalAmount = ConsoleInputHandler.getUserBigDecimalInput("Какова новая цель?");
        currentUser.getGoals().put(goalId, goalAmount);
        User updated = userService.update(currentUser.getId(), currentUser);
        if (updated != null) {
            ConsoleOutputHandler.displayMsg("Цель успешно установлена на " + goalAmount);
        } else {
            ConsoleOutputHandler.displayMsg("Не удалось установить цель.");
        }
    }

    private void displayGoal() {
        if (!currentUser.getGoals().isEmpty()) {
            List<TransactionCategory> transactionCategories = transactionCategoryService.getAll();
            ConsoleOutputHandler.displayMapWithCategories(currentUser.getGoals(), transactionCategories);
        } else {
            ConsoleOutputHandler.displayMsg("Цели не установлены.");
        }
    }

    private void editBudget() {
        ConsoleOutputHandler.displayMsg("\nРедактирование бюджета");
        String msg = currentUser.getBudget() == null ? "Установить бюджет: " : "Бюджет - " + currentUser.getBudget() + ", изменить на: ";
        BigDecimal budget = ConsoleInputHandler.getUserBigDecimalInput(msg);
        currentUser.setBudget(budget);
        User updated = userService.update(currentUser.getId(), currentUser);
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
        List<Transaction> transactions = transactionService.getAll(new TransactionFilter(currentUser.getId(), null, null, null, null, null));
        ConsoleOutputHandler.displayTransactionList(transactions);
        long transactionId = ConsoleInputHandler.getUserIntegerInput("Введите id транзакции: ");
        if (transactionService.deleteById(transactionId)) {
            ConsoleOutputHandler.displayMsg("Транзакция удалена!");
        } else {
            ConsoleOutputHandler.displayMsg("Удаление не удалось!");
        }
    }

    private void editTransaction() {
        ConsoleOutputHandler.displayMsg("\nРедактирование транзакции");
        List<Transaction> transactions = transactionService.getAll(new TransactionFilter(currentUser.getId(), null, null, null, null, null));
        ConsoleOutputHandler.displayTransactionList(transactions);
        long transactionId = ConsoleInputHandler.getUserIntegerInput("Введите id транзакции: ");
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
    }

    private void checkGoalIncome(Long transactionCategoryId) {
        if (currentUser.getGoals() != null && currentUser.getGoals().get(transactionCategoryId) != null && transactionService.isGoalIncome(currentUser.getId(), currentUser.getGoals().get(transactionCategoryId), transactionCategoryId)) {
            TransactionCategory transactionCategory = transactionCategoryService.getById(transactionCategoryId);
            String msg = "Выполнена цель " + currentUser.getGoals().get(transactionCategoryId) + " для " + transactionCategory.getCategoryName();
            ConsoleOutputHandler.displayMsg(msg);
            notificationService.sendNotification(msg);
            emailNotificationService.sendNotification(msg);
            currentUser.getGoals().remove(transactionCategoryId);
            userService.update(currentUser.getId(), currentUser);
        }
    }

    private void checkBudgetExceeded() {
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

        long userId;
        int choice;
        do {
            choice = ConsoleInputHandler.getUserIntegerInput("1 - Свои транзакции, 2 - Транзакции другого пользователя");
        } while (choice < 1 || choice > 2);
        if (choice == 1) {
            userId = currentUser.getId();
        } else {
            displayAllUsers();
            userId = ConsoleInputHandler.getUserIntegerInput("Введите id пользователя: ");
        }

        TransactionFilter filter = new TransactionFilter(userId, date, null, null, transactionCategoryId, transactionType);
        displayTransactions(filter);
    }

    private void displayTransactions(TransactionFilter filter) {
        ConsoleOutputHandler.displayMsg("\nСписок транзакций");
        List<Transaction> transactions = transactionService.getAll(filter);
        ConsoleOutputHandler.displayTransactionList(transactions);
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
        long userId = ConsoleInputHandler.getUserIntegerInput("Введите id пользователя для редактирования: ");
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
        long userId = ConsoleInputHandler.getUserIntegerInput("Введите id пользователя для удаления: ");
        if (userService.delete(userId)) {
            ConsoleOutputHandler.displayMsg("Пользователь удален.");
        } else {
            ConsoleOutputHandler.displayMsg("Ошибка: пользователь не найден.");
        }
    }

    private void deleteCategory() {
        displayAllCategories();
        long transactionCategoryId = ConsoleInputHandler.getUserIntegerInput("Введите id категории для удаления: ");
        if (transactionCategoryService.deleteById(transactionCategoryId)) {
            ConsoleOutputHandler.displayMsg("Категория удалена!");
        } else {
            ConsoleOutputHandler.displayMsg("\nУдаление не удалось!\n");
        }
    }

    private void editCategory() {
        ConsoleOutputHandler.displayMsg("\nРедактирование категории");
        displayAllCategories();
        long transactionCategoryId = ConsoleInputHandler.getUserIntegerInput("Введите id категории для редактирования: ");
        String transactionCategoryName = ConsoleInputHandler.getUserTextInput("Введите название категории: ");
        TransactionCategory updated = transactionCategoryService.update(transactionCategoryId, new TransactionCategory(transactionCategoryName));
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
        TransactionCategory transactionCategory = transactionCategoryService.save(transactionCategoryRequest);
        String msg = transactionCategory != null ?
                "\nДобавлен новый тип: " + transactionCategory.getCategoryName() + "\n" :
                "\nДобавление не удалось\n";
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void displayAllCategories() {
        ConsoleOutputHandler.displayMsg("\nСписок категорий");
        List<TransactionCategory> transactionCategories = transactionCategoryService.getAll();
        ConsoleOutputHandler.displayTransactionCategoryList(transactionCategories);
    }
}
