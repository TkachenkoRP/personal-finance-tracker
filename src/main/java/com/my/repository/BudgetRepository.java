package com.my.repository;

import com.my.model.Budget;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    List<Budget> getAll() throws SQLException;

    Optional<Budget> getById(Long id) throws SQLException;

    Budget save(Long userId, Budget entity) throws SQLException;

    Budget update(Budget entity) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    List<Budget> getAllByUserId(Long userId) throws SQLException;

    Optional<Budget> getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;

    boolean deactivateBudgetById(Long id) throws SQLException;
}
