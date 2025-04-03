package com.my.repository.impl;

import com.my.model.Budget;
import com.my.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcBudgetRepositoryImpl implements BudgetRepository {
    private final JdbcTemplate jdbcTemplate;
    @Value("${spring.datasource.hikari.schema}")
    private String schema;

    public JdbcBudgetRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Budget> getAll() {
        String query = "SELECT * FROM " + schema + ".budget";
        return jdbcTemplate.query(query, (rs, rowNum) -> mapBudget(rs));
    }

    @Override
    public Optional<Budget> getById(Long id) {
        String query = "SELECT * FROM " + schema + ".budget WHERE id = ?";
        try {
            Budget budget = jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapBudget(rs), id);
            return Optional.ofNullable(budget);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private Budget mapBudget(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getLong("id"),
                rs.getBigDecimal("total_amount"),
                rs.getDate("period_start").toLocalDate(),
                rs.getDate("period_end").toLocalDate(),
                rs.getLong("category_id"),
                rs.getBoolean("is_active")
        );
    }

    @Override
    public Budget save(Long userId, Budget entity) {
        String query = "INSERT INTO " + schema + ".budget (total_amount, period_start, period_end, user_id, category_id, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, entity.getTotalAmount());
            ps.setDate(2, Date.valueOf(entity.getPeriodStart()));
            ps.setDate(3, Date.valueOf(entity.getPeriodEnd()));
            ps.setLong(4, userId);
            ps.setLong(5, entity.getCategoryId());
            ps.setBoolean(6, entity.isActive());
            return ps;
        }, keyHolder);
        if (update > 0) {
            List<Map<String, Object>> keys = keyHolder.getKeyList();
            if (!keys.isEmpty()) {
                Map<String, Object> generatedKey = keys.get(0);
                Number generatedId = (Number) generatedKey.get("id");
                if (generatedId != null) {
                    long id = generatedId.longValue();
                    entity.setId(id);
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    public Budget update(Budget entity) {
        String query = "UPDATE " + schema + ".budget SET total_amount = ?, period_start = ?, period_end = ? WHERE id = ?";
        int update = jdbcTemplate.update(query, entity.getTotalAmount(), entity.getPeriodStart(), entity.getPeriodEnd(), entity.getId());
        if (update > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        String query = "DELETE FROM " + schema + ".budget WHERE id = ?";
        int update = jdbcTemplate.update(query, id);
        return update > 0;
    }

    @Override
    public List<Budget> getAllByUserId(Long userId) {
        String query = "SELECT * FROM " + schema + ".budget WHERE user_id = ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> mapBudget(rs), userId);
    }

    @Override
    public Optional<Budget> getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId) {
        String query = "SELECT * FROM " + schema + ".budget WHERE user_id = ? AND category_id = ? AND is_active = true";
        try {
            Budget budget = jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapBudget(rs), userId, categoryId);
            return Optional.ofNullable(budget);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deactivateBudgetById(Long id) {
        String query = "UPDATE " + schema + ".budget SET is_active = false WHERE id = ?";
        int update = jdbcTemplate.update(query, id);
        return update > 0;
    }
}
