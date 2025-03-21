package com.my.app;

import com.my.in.ConsoleInputHandler;
import com.my.model.Budget;
import com.my.model.Transaction;
import com.my.model.TransactionCategory;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.model.User;
import com.my.out.ConsoleOutputHandler;
import com.my.service.JdbcDataService;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserManager;
import com.my.service.UserService;
import com.my.service.impl.JdbcDataServiceImpl;
import liquibase.exception.LiquibaseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConsoleApp {
    private static final Logger logger = LogManager.getRootLogger();

    private final UserService userService;
    private final TransactionService transactionService;
    private final TransactionCategoryService transactionCategoryService;
    private static final Set<Integer> UNAUTHENTICATED_CHOICES = Set.of(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22);
    private static final Set<Integer> AUTHENTICATED_CHOICES = Set.of(1, 2);
    private static final Set<Integer> ADMIN_CHOICES = Set.of(22, 23, 24, 25, 26);

    public ConsoleApp(UserService userService, TransactionService transactionService,
                      TransactionCategoryService transactionCategoryService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.transactionCategoryService = transactionCategoryService;
    }

    public void start() {
        init();
        boolean working = true;
        while (working) {
            ConsoleOutputHandler.displayMenu();
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка инициализации БД! {0}", e.getMessage()));
        }
    }

    private boolean handleUserChoice(int choice) {
        if (!UserManager.isLoggedIn() && UNAUTHENTICATED_CHOICES.contains(choice)) {
            choice = -1;
        }
        if (UserManager.isLoggedIn() && AUTHENTICATED_CHOICES.contains(choice)) {
            choice = -1;
        }
        if (UserManager.isAdmin() && ADMIN_CHOICES.contains(choice)) {
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса блокировки пользователя! {0}", e.getMessage()));
        }
    }

    private void displayFinancialReport() {
        LocalDate[] dates = getInputDates();
        try {
            Map<String, BigDecimal> report = transactionService.generateFinancialReport(UserManager.getLoggedInUser().getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMap(report);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса финансового отчета.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса финансового отчета! {0}", e.getMessage()));
        }
    }

    private void displayAnalyzeExpensesByCategory() {
        LocalDate[] dates = getInputDates();
        try {
            Map<String, BigDecimal> analyzed = transactionService.analyzeExpensesByCategory(UserManager.getLoggedInUser().getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMap(analyzed);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса анализа по категориям.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса анализа по категориям! {0}", e.getMessage()));
        }

    }

    private void displayTotalExpenses() {
        LocalDate[] dates = getInputDates();
        try {
            BigDecimal expenses = transactionService.getTotalExpenses(UserManager.getLoggedInUser().getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMsg("Расходы с " + dates[0] + " до " + dates[1] + ": " + expenses);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса расходов.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса расходов! {0}", e.getMessage()));
        }
    }

    private void displayTotalIncome() {
        LocalDate[] dates = getInputDates();
        try {
            BigDecimal income = transactionService.getTotalIncome(UserManager.getLoggedInUser().getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMsg("Доход с " + dates[0] + " до " + dates[1] + ": " + income);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса доходов.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса доходов! {0}", e.getMessage()));
        }
    }

    private void displayBalanceByDate() {
        LocalDate[] dates = getInputDates();

        try {
            BigDecimal income = transactionService.getTotalIncome(UserManager.getLoggedInUser().getId(), dates[0], dates[1]);
            BigDecimal expenses = transactionService.getTotalExpenses(UserManager.getLoggedInUser().getId(), dates[0], dates[1]);
            ConsoleOutputHandler.displayMsg("Баланс c " + dates[0] + " по " + dates[1] + ": " + income.subtract(expenses));
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса баланса.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса баланса! {0}", e.getMessage()));
        }
    }

    private LocalDate[] getInputDates() {
        LocalDate from = ConsoleInputHandler.getUserDateInput("Введите дату начала периода");
        LocalDate to = ConsoleInputHandler.getUserDateInput("Введите дату конца периода");
        return new LocalDate[]{from, to};
    }

    private void displayBalance() {
        try {
            BigDecimal balance = transactionService.getBalance(UserManager.getLoggedInUser().getId());
            ConsoleOutputHandler.displayMsg("Баланс: " + balance);
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса баланса.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса баланса! {0}", e.getMessage()));
        }
    }

    private void editGoal() {
        ConsoleOutputHandler.displayMsg("\nРедактирование цели накопления");
    }

    private void displayGoal() {
        if (!UserManager.getLoggedInUser().getGoals().isEmpty()) {
            ConsoleOutputHandler.displayGoalsList(UserManager.getLoggedInUser().getGoals());
        } else {
            ConsoleOutputHandler.displayMsg("Цели не установлены.");
        }
    }

    private void editBudget() {
        ConsoleOutputHandler.displayMsg("\nРедактирование бюджета");
    }

    private void displayBudget() {
        List<Budget> budgets = UserManager.getLoggedInUser().getBudgets();
        if (budgets.isEmpty()) {
            ConsoleOutputHandler.displayMsg("Бюджет не установлен");
        }
        ConsoleOutputHandler.displayBudgetList(budgets);
    }

    private void deleteTransaction() {
        ConsoleOutputHandler.displayMsg("\nУдаление транзакции");
        displayTransactions(new TransactionFilter(UserManager.getLoggedInUser().getId()));
        long transactionId = ConsoleInputHandler.getUserIntegerInput("Введите id транзакции: ");
        try {
            if (transactionService.deleteById(transactionId)) {
                ConsoleOutputHandler.displayMsg("Транзакция удалена!");
            } else {
                ConsoleOutputHandler.displayMsg("Удаление не удалось!");
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса удаления транзакции.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса удаления транзакции! {0}", e.getMessage()));
        }
    }

    private void editTransaction() {
        ConsoleOutputHandler.displayMsg("\nРедактирование транзакции");
        displayTransactions(new TransactionFilter(UserManager.getLoggedInUser().getId()));
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса редактирования транзакции! {0}", e.getMessage()));
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
            transaction.setUser(UserManager.getLoggedInUser());

            transactionService.save(transaction);
            String msg = transactionService.processTransaction(transaction);
            if (msg != null) {
                ConsoleOutputHandler.displayMsg(msg);
            }
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса добавления транзакции.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса добавления транзакции! {0}", e.getMessage()));
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

        long userId = UserManager.getLoggedInUser().getId();

        if (UserManager.isAdmin()) {
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса транзакций! {0}", e.getMessage()));
        }
    }


    private void registrationUser() {
        ConsoleOutputHandler.displayMsg("\nРегистрация пользователя");
        String userEmail = ConsoleInputHandler.getUserTextInput("Введите email пользователя: ");
        String userName = ConsoleInputHandler.getUserTextInput("Введите имя пользователя: ");
        String password = ConsoleInputHandler.getUserTextInput("Введите пароль: ");
        try {
            UserManager.setLoggedInUser(userService.registration(userEmail, userName, password));
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса регистрации пользователя.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса регистрации пользователя! {0}", e.getMessage()));
        }
        String msg = UserManager.isLoggedIn() ?
                "\nРегистрация " + UserManager.getLoggedInUser().getEmail() + " прошла успешно!\n" :
                "\nРегистрация не удалась\n";
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void loginUser() {
        ConsoleOutputHandler.displayMsg("\nВход");
        String userEmail = ConsoleInputHandler.getUserTextInput("Введите email пользователя: ");
        String password = ConsoleInputHandler.getUserTextInput("Введите пароль: ");
        try {
            UserManager.setLoggedInUser(userService.login(userEmail, password));
        } catch (SQLException e) {
            ConsoleOutputHandler.displayMsg("Ошибка запроса авторизации пользователя.");
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса авторизации пользователя! {0}", e.getMessage()));
        }
        String msg = UserManager.isLoggedIn() ?
                "\nУспешный вход!\n" :
                "\nНеудачный вход!\n";
        ConsoleOutputHandler.displayMsg(msg);
    }

    private void editCurrentUserData() {
        ConsoleOutputHandler.displayMsg("\nРедактирование пользователя " + UserManager.getLoggedInUser().getName());
        editUserData(UserManager.getLoggedInUser());
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса редактирования данных пользователя! {0}", e.getMessage()));
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса редактирования данных пользователя! {0}", e.getMessage()));
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса выборки всех пользователей! {0}", e.getMessage()));
        }
    }

    private boolean exit() {
        if (UserManager.isLoggedIn()) {
            UserManager.setLoggedInUser(null);
            return true;
        }
        return false;
    }

    private void deleteCurrentUser() {
        try {
            if (userService.delete(UserManager.getLoggedInUser().getId())) {
                UserManager.setLoggedInUser(null);
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса удаления пользователя! {0}", e.getMessage()));
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса удаления категории! {0}", e.getMessage()));
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса редактирования категории! {0}", e.getMessage()));
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса добавления категории! {0}", e.getMessage()));
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
            logger.log(Level.ERROR, MessageFormat.format("Ошибка запроса всех категорий! {0}", e.getMessage()));
        }
    }
}
