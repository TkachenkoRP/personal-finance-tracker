package com.my.repository.impl;

import com.my.annotation.Loggable;
import com.my.configuration.AppConfiguration;
import com.my.model.Budget;
import com.my.repository.BudgetRepository;
import com.my.util.DBUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class JdbcBudgetRepositoryImpl implements BudgetRepository {
    private static final Logger logger = LogManager.getRootLogger();
    private final Connection connection;

    private final String schema;

    public JdbcBudgetRepositoryImpl() {
        this(DBUtil.getConnection());
    }

    public JdbcBudgetRepositoryImpl(Connection connection) {
        this.connection = connection;
        this.schema = AppConfiguration.getProperty("database.schema");
    }

    @Override
    public List<Budget> getAll() throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT * FROM " + schema + ".budget";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Budget budget = mapRowToBudget(resultSet);
                budgets.add(budget);
            }
        }
        return budgets;
    }

    private Budget mapRowToBudget(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");
        LocalDate periodStart = Optional.ofNullable(resultSet.getDate("period_start"))
                .map(java.sql.Date::toLocalDate)
                .orElse(null);
        LocalDate periodEnd = Optional.ofNullable(resultSet.getDate("period_end"))
                .map(java.sql.Date::toLocalDate)
                .orElse(null);
        long categoryId = resultSet.getLong("category_id");
        boolean active = resultSet.getBoolean("is_active");

        Budget budget = new Budget();
        budget.setId(id);
        budget.setTotalAmount(totalAmount);
        budget.setPeriodStart(periodStart);
        budget.setPeriodEnd(periodEnd);
        budget.setCategoryId(categoryId);
        budget.setActive(active);

        return budget;
    }

    @Override
    public Optional<Budget> getById(Long id) throws SQLException {
        String query = "SELECT * FROM " + schema + ".budget WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Budget budget = mapRowToBudget(resultSet);
                    return Optional.of(budget);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Budget save(Long userId, Budget entity) {
        try {
            connection.setAutoCommit(false);
            String query = "INSERT INTO " + schema + ".budget (total_amount, period_start, period_end, user_id, category_id, is_active) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setBigDecimal(1, entity.getTotalAmount());
                statement.setDate(2, Date.valueOf(entity.getPeriodStart()));
                statement.setDate(3, Date.valueOf(entity.getPeriodEnd()));
                statement.setLong(4, userId);
                statement.setLong(5, entity.getCategoryId());
                statement.setBoolean(6, entity.isActive());

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));
                        connection.commit();
                        return entity;
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.log(Level.ERROR, rollbackEx);
            }
            logger.log(Level.ERROR, e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.ERROR, e);
            }
        }
        return null;
    }

    @Override
    public Budget update(Budget entity) throws SQLException {
        String query = "UPDATE " + schema + ".budget SET total_amount = ?, period_start = ?, period_end = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, entity.getTotalAmount());
            statement.setDate(2, Date.valueOf(entity.getPeriodStart()));
            statement.setDate(3, Date.valueOf(entity.getPeriodEnd()));
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
        String query = "DELETE FROM " + schema + ".budget WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<Budget> getAllByUserId(Long userId) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT * FROM " + schema + ".budget WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Budget budget = mapRowToBudget(resultSet);
                    budgets.add(budget);
                }
            }
        }
        return budgets;
    }

    @Override
    public Optional<Budget> getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException {
        String query = "SELECT * FROM " + schema + ".budget WHERE user_id = ? AND category_id = ? AND is_active = true";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, categoryId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Budget budget = mapRowToBudget(resultSet);
                return Optional.of(budget);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deactivateBudgetById(Long id) throws SQLException {
        String query = "UPDATE " + schema + ".budget SET is_active = false WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}
