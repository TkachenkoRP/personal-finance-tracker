package com.my.repository.impl;

import com.my.configuration.AppConfiguration;
import com.my.model.Goal;
import com.my.model.TransactionCategory;
import com.my.repository.GoalRepository;
import com.my.repository.TransactionCategoryRepository;
import com.my.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGoalRepositoryImpl implements GoalRepository {
    private final Connection connection;
    private final TransactionCategoryRepository transactionCategoryRepository;

    private final String schema;

    public JdbcGoalRepositoryImpl() {
        this(new JdbcTransactionCategoryRepository());
    }

    public JdbcGoalRepositoryImpl(TransactionCategoryRepository transactionCategoryRepository) {
        this(DBUtil.getConnection(), transactionCategoryRepository);
    }

    public JdbcGoalRepositoryImpl(Connection connection, TransactionCategoryRepository transactionCategoryRepository) {
        this.connection = connection;
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.schema = AppConfiguration.getProperty("database.schema");
    }

    @Override
    public List<Goal> getAll() throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String query = "SELECT * FROM " + schema + ".goal";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Goal goal = mapRowToGoal(resultSet);
                goals.add(goal);
            }
        }
        return goals;
    }

    @Override
    public Optional<Goal> getById(Long id) throws SQLException {
        String query = "SELECT * FROM " + schema + ".goal WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Goal goal = mapRowToGoal(resultSet);
                    return Optional.of(goal);
                }
            }
        }
        return Optional.empty();
    }

    private Goal mapRowToGoal(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        BigDecimal amount = resultSet.getBigDecimal("target_amount");
        TransactionCategory transactionCategory =
                transactionCategoryRepository.getById(resultSet.getLong("category_id")).orElse(null);

        Goal goal = new Goal();
        goal.setId(id);
        goal.setTargetAmount(amount);
        goal.setCategory(transactionCategory);

        return goal;
    }

    @Override
    public Goal save(Long userId, Goal entity) throws SQLException {
        String query = "INSERT INTO " + schema + ".goal (target_amount, category_id, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setBigDecimal(1, entity.getTargetAmount());
            statement.setLong(2, entity.getCategory().getId());
            statement.setLong(3, userId);

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
    public Goal update(Goal entity) throws SQLException {
        String query = "UPDATE " + schema + ".goal SET target_amount = ?, category_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, entity.getTargetAmount());
            statement.setLong(2, entity.getCategory().getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String query = "DELETE FROM " + schema + ".goal WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<Goal> getAllByUserId(Long userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String query = "SELECT * FROM " + schema + ".goal WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Goal goal = mapRowToGoal(resultSet);
                    goals.add(goal);
                }
            }
        }
        return goals;
    }

    @Override
    public Optional<Goal> getActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException {
        String query = "SELECT * FROM " + schema + ".goal WHERE user_id = ? AND category_id = ? AND is_active = true";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, categoryId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Goal goal = mapRowToGoal(resultSet);
                return Optional.of(goal);
            }
        }
        return Optional.empty();
    }
}
