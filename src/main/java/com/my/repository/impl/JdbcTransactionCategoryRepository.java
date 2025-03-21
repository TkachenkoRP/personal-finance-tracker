package com.my.repository.impl;

import com.my.configuration.AppConfiguration;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;
import com.my.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTransactionCategoryRepository implements TransactionCategoryRepository {

    private final Connection connection;
    private final String schema;

    public JdbcTransactionCategoryRepository() {
        this(DBUtil.getConnection());
    }

    public JdbcTransactionCategoryRepository(Connection connection) {
        this.connection = connection;
        schema = AppConfiguration.getProperty("database.schema");
    }

    @Override
    public boolean existsByCategoryNameIgnoreCase(String categoryName) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + schema + ".transaction_category WHERE LOWER(category_name) = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public List<TransactionCategory> getAll() throws SQLException {
        List<TransactionCategory> categories = new ArrayList<>();
        String query = "SELECT id, category_name FROM " + schema + ".transaction_category";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                TransactionCategory transactionCategory = mapRowToTransactionCategory(resultSet);
                categories.add(transactionCategory);
            }
        }
        return categories;
    }

    @Override
    public Optional<TransactionCategory> getById(Long id) throws SQLException {
        String query = "SELECT id, category_name FROM " + schema + ".transaction_category WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                TransactionCategory transactionCategory = mapRowToTransactionCategory(resultSet);
                return Optional.of(transactionCategory);
            }
        }
        return Optional.empty();
    }

    private TransactionCategory mapRowToTransactionCategory(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String categoryName = resultSet.getString("category_name");

        TransactionCategory transactionCategory = new TransactionCategory();
        transactionCategory.setId(id);
        transactionCategory.setCategoryName(categoryName);

        return transactionCategory;
    }

    @Override
    public TransactionCategory save(TransactionCategory entity) throws SQLException {
        String query = "INSERT INTO " + schema + ".transaction_category (category_name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getCategoryName());
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
    public TransactionCategory update(TransactionCategory entity) throws SQLException {
        String query = "UPDATE " + schema + ".transaction_category SET category_name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getCategoryName());
            statement.setLong(2, entity.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String query = "DELETE FROM " + schema + ".transaction_category WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
}
