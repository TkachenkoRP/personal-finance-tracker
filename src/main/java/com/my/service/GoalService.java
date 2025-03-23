package com.my.service;

import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.model.Goal;

import java.sql.SQLException;
import java.util.List;

public interface GoalService {
    List<Goal> geAll() throws SQLException;

    Goal getEntityById(Long id) throws SQLException;

    GoalResponseDto getById(Long id) throws SQLException;

    GoalResponseDto save(Long userId, GoalRequestDto goal) throws SQLException;

    GoalResponseDto update(Long id, GoalRequestDto sourceGoal) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    List<GoalResponseDto> getAllGoalsByUserId(Long userId) throws SQLException;

    String getGoalIncomeInfo(Long userId, Long categoryId) throws SQLException;

    Goal getAllActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException;
}
