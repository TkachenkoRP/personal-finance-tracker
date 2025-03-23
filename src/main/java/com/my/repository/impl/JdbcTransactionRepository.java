package com.my.repository.impl;

import com.my.annotation.Loggable;
import com.my.configuration.AppConfiguration;
import com.my.dto.ExpenseAnalysisDto;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.TransactionRepository;
import com.my.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Loggable
public class JdbcTransactionRepository implements TransactionRepository {
    private final Connection connection;

    private final String schema;

    public JdbcTransactionRepository() {
        this(DBUtil.getConnection());
    }

    public JdbcTransactionRepository(Connection connection) {
        this.connection = connection;
        schema = AppConfiguration.getProperty("database.schema");
    }

    @Override
    public List<Transaction> getAll() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                       "FROM " + schema + ".transaction t " +
                       "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                       "JOIN " + schema + ".user u ON t.user_id = u.id";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Transaction transaction = mapRowToTransaction(resultSet);
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> getAll(TransactionFilter filter) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                                                "FROM " + schema + ".transaction t " +
                                                "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                                                "JOIN " + schema + ".user u ON t.user_id = u.id");

        List<Object> parameters = new ArrayList<>();

        if (filter != null) {
            if (filter.userId() != null) {
                query.append(" AND t.user_id = ?");
                parameters.add(filter.userId());
            }
            if (filter.categoryId() != null) {
                query.append(" AND t.category_id = ?");
                parameters.add(filter.categoryId());
            }
            if (filter.date() != null) {
                query.append(" AND t.date = ?");
                parameters.add(Date.valueOf(filter.date()));
            }
            if (filter.from() != null) {
                query.append(" AND t.date >= ?");
                parameters.add(Date.valueOf(filter.from()));
            }
            if (filter.to() != null) {
                query.append(" AND t.date <= ?");
                parameters.add(Date.valueOf(filter.to()));
            }
            if (filter.type() != null) {
                query.append(" AND t.type = ?");
                parameters.add(filter.type().name());
            }
        }

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = mapRowToTransaction(resultSet);
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    @Override
    public Optional<Transaction> getById(Long id) throws SQLException {
        String query = "SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                       "FROM " + schema + ".transaction t " +
                       "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                       "JOIN " + schema + ".user u ON t.user_id = u.id " +
                       "WHERE t.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Transaction transaction = mapRowToTransaction(resultSet);
                return Optional.of(transaction);
            }
        }
        return Optional.empty();
    }

    private Transaction mapRowToTransaction(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        LocalDate date = resultSet.getDate("date").toLocalDate();
        TransactionType transactionType = TransactionType.valueOf(resultSet.getString("type"));
        BigDecimal amount = resultSet.getBigDecimal("amount");
        String description = resultSet.getString("description");
        long userId = resultSet.getLong("user_id");
        long transactionCategoryId = resultSet.getLong("category_id");

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setDate(date);
        transaction.setType(transactionType);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCategoryId(transactionCategoryId);
        transaction.setUserId(userId);

        return transaction;
    }

    @Override
    public Transaction save(Transaction entity) throws SQLException {
        String query = "INSERT INTO " + schema + ".transaction (amount, description, date, type, category_id, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setBigDecimal(1, entity.getAmount());
            statement.setString(2, entity.getDescription());
            statement.setDate(3, Date.valueOf(entity.getDate()));
            statement.setString(4, entity.getType().name());
            statement.setLong(5, entity.getCategoryId());
            statement.setLong(6, entity.getUserId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    public Transaction update(Transaction entity) throws SQLException {
        String query = "UPDATE " + schema + ".transaction SET amount = ?, description = ?, category_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, entity.getAmount());
            statement.setString(2, entity.getDescription());
            statement.setLong(3, entity.getCategoryId());
            statement.setLong(4, entity.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String query = "DELETE FROM " + schema + ".transaction WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public BigDecimal getMonthExpense(Long userId) throws SQLException {
        BigDecimal totalExpense = BigDecimal.ZERO;
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        String query = "SELECT SUM(amount) AS total_expense " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND EXTRACT(MONTH FROM t.date) = ? " +
                       "AND EXTRACT(YEAR FROM t.date) = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setString(2, TransactionType.EXPENSE.name());
            statement.setInt(3, currentMonth);
            statement.setInt(4, currentYear);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalExpense = resultSet.getBigDecimal("total_expense");
                }
            }
        }

        return totalExpense != null ? totalExpense : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getBalance(Long userId) throws SQLException {
        BigDecimal balance = BigDecimal.ZERO;

        String query = "SELECT SUM(CASE WHEN t.type = ? THEN t.amount ELSE -t.amount END) AS balance " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, TransactionType.INCOME.name());
            statement.setLong(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    balance = resultSet.getBigDecimal("balance");
                }
            }
        }

        return balance != null ? balance : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException {
        BigDecimal totalIncome = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) AS total_income " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND t.date >= ? AND t.date <= ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setString(2, TransactionType.INCOME.name());
            statement.setDate(3, Date.valueOf(from));
            statement.setDate(4, Date.valueOf(to));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalIncome = resultSet.getBigDecimal("total_income");
                }
            }
        }

        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException {
        BigDecimal totalExpenses = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) AS total_expenses " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND t.date >= ? AND t.date <= ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setString(2, TransactionType.EXPENSE.name());
            statement.setDate(3, Date.valueOf(from));
            statement.setDate(4, Date.valueOf(to));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalExpenses = resultSet.getBigDecimal("total_expenses");
                }
            }
        }

        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    @Override
    public List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException {
        List<ExpenseAnalysisDto> result = new ArrayList<>();

        String query = "SELECT tc.category_name, SUM(t.amount) AS total_expenses " +
                       "FROM " + schema + ".transaction t " +
                       "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND t.date >= ? AND t.date <= ? " +
                       "GROUP BY tc.category_name";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setString(2, TransactionType.EXPENSE.name());
            statement.setDate(3, Date.valueOf(from));
            statement.setDate(4, Date.valueOf(to));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryName = resultSet.getString("category_name");
                    BigDecimal totalExpenses = resultSet.getBigDecimal("total_expenses");
                    result.add(new ExpenseAnalysisDto(categoryName, totalExpenses));
                }
            }
        }

        return result;
    }

    @Override
    public BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId) throws SQLException {
        BigDecimal totalIncome = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) AS total_income " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.category_id = ? AND t.type = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, transactionCategoryId);
            statement.setString(3, TransactionType.INCOME.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalIncome = resultSet.getBigDecimal("total_income");
                }
            }
        }

        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }
}
