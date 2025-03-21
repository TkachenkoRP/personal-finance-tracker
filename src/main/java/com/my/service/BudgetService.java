package com.my.service;

import com.my.model.Budget;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BudgetService {
    List<Budget> geAll() throws SQLException;

    Budget getById(Long id) throws SQLException;

    Budget create(Long userId, Budget goal) throws SQLException;

    Budget update(Long id, Budget sourceBudget) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    List<Budget> getAllBudgetsByUserId(Long userId) throws SQLException;

    String getBudgetsExceededInfo(Long userId, Long categoryId) throws SQLException;

    Budget getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;
}
