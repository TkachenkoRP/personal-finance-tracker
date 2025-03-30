package com.my.repository.impl;

import com.my.annotation.Loggable;
import com.my.model.Goal;
import com.my.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Loggable
@Repository
public class JdbcGoalRepositoryImpl implements GoalRepository {
    private final JdbcTemplate jdbcTemplate;
    @Value("${datasource.schema}")
    private String schema;

    public JdbcGoalRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Goal> getAll() {
        String query = "SELECT * FROM " + schema + ".goal";
        return jdbcTemplate.query(query, (rs, rowNum) -> mapGoal(rs));
    }

    @Override
    public Optional<Goal> getById(Long id) {
        String query = "SELECT * FROM " + schema + ".goal WHERE id = ?";
        try {
            Goal goal = jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapGoal(rs), id);
            return Optional.ofNullable(goal);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private Goal mapGoal(ResultSet rs) throws SQLException {
        return new Goal(
                rs.getLong("id"),
                rs.getBigDecimal("amount"),
                rs.getLong("category_id"),
                rs.getBoolean("is_active")
        );
    }

    @Override
    public Goal save(Long userId, Goal entity) {
        String query = "INSERT INTO " + schema + ".goal (amount, category_id, user_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, entity.getTargetAmount());
            ps.setLong(2, entity.getCategoryId());
            ps.setLong(3, userId);
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
    public Goal update(Goal entity) {
        String query = "UPDATE " + schema + ".goal SET amount = ?, category_id = ? WHERE id = ?";
        int update = jdbcTemplate.update(query, entity.getTargetAmount(), entity.getCategoryId(), entity.getId());
        if (update > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        String query = "DELETE FROM " + schema + ".goal WHERE id = ?";
        int update = jdbcTemplate.update(query, id);
        return update > 0;
    }

    @Override
    public List<Goal> getAllByUserId(Long userId) {
        String query = "SELECT * FROM " + schema + ".goal WHERE user_id = ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> mapGoal(rs), userId);
    }

    @Override
    public Optional<Goal> getActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) {
        String query = "SELECT * FROM " + schema + ".goal WHERE user_id = ? AND category_id = ? AND is_active = true";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapGoal(rs), userId, categoryId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
