package com.my.repository;

import com.my.model.Goal;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    List<Goal> getAll() throws SQLException;

    Optional<Goal> getById(Long id) throws SQLException;

    Goal save(Long userId, Goal entity) throws SQLException;

    Goal update(Goal entity) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    List<Goal> getAllByUserId(Long userId) throws SQLException;

    Optional<Goal> getActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;
}
