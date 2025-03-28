package com.my.repository.impl;

import com.my.annotation.Loggable;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.BudgetRepository;
import com.my.repository.GoalRepository;
import com.my.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    @Value("${datasource.schema}")
    private String schema;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;

    @Autowired
    public JdbcUserRepository(DataSource dataSource, BudgetRepository budgetRepository, GoalRepository goalRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM " + schema + ".user";
        return jdbcTemplate.query(query, (rs, rowNum) -> mapUser(rs));
    }

    @Override
    public Optional<User> getById(Long id) {
        String query = "SELECT * FROM " + schema + ".user WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapUser(rs), id);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        return new User(
                id,
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name"),
                budgetRepository.getAllByUserId(id),
                goalRepository.getAllByUserId(id),
                UserRole.valueOf(rs.getString("role")),
                rs.getBoolean("blocked")
        );
    }

    @Override
    public User save(User entity) {
        String query = "INSERT INTO " + schema + ".user (email, password, name, role, blocked) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getEmail());
            ps.setString(2, entity.getPassword());
            ps.setString(3, entity.getName());
            ps.setString(4, entity.getRole().name());
            ps.setBoolean(5, entity.isBlocked());
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
    public User update(User entity) {
        String query = "UPDATE " + schema + ".user SET email = ? , password = ?, name = ?, role = ?, blocked = ? WHERE id = ?";
        int update = jdbcTemplate.update(query,
                entity.getEmail(),
                entity.getPassword(),
                entity.getName(),
                entity.getRole().name(),
                entity.isBlocked(),
                entity.getId());
        if (update > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        String query = "DELETE FROM " + schema + ".user WHERE id = ?";
        int update = jdbcTemplate.update(query, id);
        return update > 0;
    }

    @Override
    public boolean isEmailOccupied(String email) {
        String query = "SELECT COUNT(*) FROM " + schema + ".user WHERE LOWER(email) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public Optional<User> getByEmailAndPassword(String email, String password) {
        String query = "SELECT * FROM " + schema + ".user WHERE LOWER(email) = LOWER(?) AND password = ?";
        try {
            User user = jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapUser(rs), email, password);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean blockUserById(Long userId) {
        String query = "UPDATE " + schema + ".user SET blocked = true WHERE id = ?";
        return jdbcTemplate.update(query, userId) > 0;
    }

    @Override
    public boolean unBlockUserById(long userId) {
        String query = "UPDATE " + schema + ".user SET blocked = false WHERE id = ?";
        return jdbcTemplate.update(query, userId) > 0;
    }
}
