package com.my.repository.impl;

import com.my.configuration.AppConfiguration;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private final Connection connection;
    private final String schema;

    public JdbcUserRepository(Connection connection) {
        this.connection = connection;
        schema = AppConfiguration.getProperty("database.schema");
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM " + schema + ".user";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = mapRowToUser(resultSet);
                    users.add(user);
                }
            }
        }
        return users;
    }

    @Override
    public Optional<User> getById(Long id) throws SQLException {
        String query = "SELECT * FROM " + schema + ".user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapRowToUser(resultSet);
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        String name = resultSet.getString("name");
        UserRole role = UserRole.valueOf(resultSet.getString("role"));
        boolean blocked = resultSet.getBoolean("blocked");
        BigDecimal budget = resultSet.getBigDecimal("budget");

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setRole(role);
        user.setBlocked(blocked);
        user.setBudget(budget);

        return user;
    }

    @Override
    public User save(User entity) throws SQLException {
        String query = "INSERT INTO " + schema + ".user (email, password, name, role, blocked) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getEmail());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getName());
            statement.setString(4, entity.getRole().name());
            statement.setBoolean(5, entity.isBlocked());
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
    public User update(User entity) throws SQLException {
        String query = "UPDATE " + schema + ".user SET email = ? , password = ?, name = ?, role = ?, budget = ?, blocked = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getEmail());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getName());
            statement.setString(4, entity.getRole().name());
            statement.setBigDecimal(5, entity.getBudget());
            statement.setBoolean(6, entity.isBlocked());
            statement.setLong(7, entity.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return entity;
            }
            return null;
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String query = "DELETE FROM " + schema + ".user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean isPresentByEmail(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + schema + ".user WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public Optional<User> getByEmailAndPassword(String email, String password) throws SQLException {
        String query = "SELECT * FROM " + schema + ".user WHERE email = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapRowToUser(resultSet);
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }
}
