package com.my.repository.impl;

import com.my.configuration.AppConfiguration;
import com.my.model.Transaction;
import com.my.model.TransactionCategory;
import com.my.model.TransactionType;
import com.my.model.User;
import com.my.repository.TransactionCategoryRepository;
import com.my.repository.TransactionRepository;
import com.my.repository.UserRepository;

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

public class JdbcTransactionRepository implements TransactionRepository {
    private final Connection connection;
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final UserRepository userRepository;

    private final String schema;

    public JdbcTransactionRepository(Connection connection, TransactionCategoryRepository transactionCategoryRepository, UserRepository userRepository) {
        this.connection = connection;
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.userRepository = userRepository;
        schema = AppConfiguration.getProperty("database.schema");
    }

    @Override
    public List<Transaction> getAll() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                "FROM " + schema + ".transaction t " +
                "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                "JOIN " + schema + ".user u ON t.user_id = u.id ";
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
    public Optional<Transaction> getById(Long id) throws SQLException {
        String query = "SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                "FROM " + schema + ".transaction t " +
                "JOIN " + schema + ".transaction_category tc ON t.type = tc.id " +
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
        User user = userRepository.getById(resultSet.getLong("user_id")).orElse(null);
        TransactionCategory transactionCategory =
                transactionCategoryRepository.getById(resultSet.getLong("category_id")).orElse(null);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setDate(date);
        transaction.setType(transactionType);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCategory(transactionCategory);
        transaction.setUser(user);

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
            statement.setLong(5, entity.getCategory().getId());
            statement.setLong(6, entity.getUser().getId());

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
            statement.setLong(3, entity.getCategory().getId());
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
}
