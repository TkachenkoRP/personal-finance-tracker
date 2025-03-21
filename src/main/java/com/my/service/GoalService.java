package com.my.service;

import com.my.model.Goal;

import java.sql.SQLException;
import java.util.List;

public interface GoalService {
    List<Goal> geAll() throws SQLException;

    Goal getById(Long id) throws SQLException;

    Goal create(Long userId, Goal goal) throws SQLException;

    Goal update(Long id, Goal sourceGoal) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    List<Goal> getAllGoalsByUserId(Long userId) throws SQLException;

    String getGoalIncomeInfo(Long userId, Long categoryId) throws SQLException;

    Goal getAllActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;
}
