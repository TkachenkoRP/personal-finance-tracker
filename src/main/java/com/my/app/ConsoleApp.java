package com.my.app;

import com.my.in.ConsoleInputHandler;
import com.my.model.Transaction;
import com.my.model.TransactionCategory;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.model.User;
import com.my.out.ConsoleOutputHandler;
import com.my.service.TransactionCategoryService;
import com.my.service.TransactionService;
import com.my.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ConsoleApp {
    private User currentUser = null;
    private final UserService userService;
    private final TransactionService transactionService;
    private final TransactionCategoryService transactionCategoryService;
    private static final Set<Integer> UNAUTHENTICATED_CHOICES = Set.of(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    private static final Set<Integer> AUTHENTICATED_CHOICES = Set.of(1, 2);

    public ConsoleApp(UserService userService, TransactionService transactionService, TransactionCategoryService transactionCategoryService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.transactionCategoryService = transactionCategoryService;
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
            case 0 -> {
                return exit();
            }
            default -> ConsoleOutputHandler.displayMsg("Неверный выбор!\n");
        }
        return true;
    }

    private void deleteTransaction() {
        ConsoleOutputHandler.displayMsg("\nУдаление транзакции");
        List<Transaction> transactions = transactionService.getAll(new TransactionFilter(currentUser.getId(), null, null, null));
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
        List<Transaction> transactions = transactionService.getAll(new TransactionFilter(currentUser.getId(), null, null, null));
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
        LocalDate date = ConsoleInputHandler.getUserDateInput("Введите дату: ");
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
    }

    private void displayAllTransactions() {
        ConsoleOutputHandler.displayMsg("\nЗадайте параметры фильтра: ");

        boolean useCategory = ConsoleInputHandler.getUserBooleanInput("Использовать фильтр по категории? ('y'/'n' или 'д'/'н'.)");
        TransactionCategory transactionCategory = null;
        if (useCategory) {
            displayAllCategories();
            int categoryId = ConsoleInputHandler.getUserIntegerInput("Введите id категории (или 0 для игнорирования): ");
            transactionCategory = transactionCategoryService.getById((long) categoryId);
        }

        boolean useDate = ConsoleInputHandler.getUserBooleanInput("Использовать фильтр по дате? ('y'/'n' или 'д'/'н'.)");
        LocalDate date = null;
        if (useDate) {
            date = ConsoleInputHandler.getUserDateInput("Введите дату: ");
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

        TransactionFilter filter = new TransactionFilter(userId, date, transactionCategory, transactionType);
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
