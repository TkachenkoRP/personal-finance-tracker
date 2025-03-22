package com.my.service;

import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.model.Budget;

import java.sql.SQLException;
import java.util.List;

public interface BudgetService {
    List<BudgetResponseDto> geAll() throws SQLException;

    BudgetResponseDto getById(Long id) throws SQLException;

    Budget getEntityById(Long id) throws SQLException;

    BudgetResponseDto save(Long userId, BudgetRequestDto budget) throws SQLException;

    BudgetResponseDto update(Long id, BudgetRequestDto sourceBudget) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    List<BudgetResponseDto> getAllBudgetsByUserId(Long userId) throws SQLException;

    String getBudgetsExceededInfo(Long userId, Long categoryId) throws SQLException;

    boolean deactivateBudget(Long id) throws SQLException;
}
