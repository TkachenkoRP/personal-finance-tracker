package com.my.service.impl;

import com.my.annotation.Loggable;
import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.exception.AccessDeniedException;
import com.my.exception.EntityNotFoundException;
import com.my.mapper.BudgetMapper;
import com.my.model.Budget;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.BudgetRepository;
import com.my.repository.TransactionRepository;
import com.my.service.BudgetService;
import com.my.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

@Loggable
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private static final Logger logger = LogManager.getRootLogger();
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService emailNotificationService;
    private final BudgetMapper budgetMapper;

    private static final String BUDGET_NOT_FOUND = "Бюджет с id {0} не найден";

    @Override
    public List<BudgetResponseDto> geAll() {
        List<Budget> budgets = budgetRepository.getAll();
        List<BudgetResponseDto> responseDtoList = budgetMapper.toDto(budgets);
        logger.log(Level.DEBUG, "Get all budgets");
        return responseDtoList;
    }

    @Override
    public BudgetResponseDto getById(Long id) {
        Budget budget = getEntityById(id);
        BudgetResponseDto responseDto = budgetMapper.toDto(budget);
        logger.log(Level.DEBUG, "Get budget by id: {}", responseDto);
        return responseDto;
    }

    @Override
    public Budget getEntityById(Long id) {
        Budget budget = budgetRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(BUDGET_NOT_FOUND, id))
        );
        logger.log(Level.DEBUG, "Get entity budget by id: {}", budget);
        return budget;
    }

    @Override
    public BudgetResponseDto save(Long userId, BudgetRequestDto budget) {
        if (userId == null) {
            throw new AccessDeniedException("У Вас нет права доступа");
        }
        Budget requestEntity = budgetMapper.toEntity(budget);
        requestEntity.setActive(true);
        Budget saved = budgetRepository.save(userId, requestEntity);
        BudgetResponseDto responseDto = budgetMapper.toDto(saved);
        logger.log(Level.DEBUG, "Save budget: {}", responseDto);
        return responseDto;
    }

    @Override
    public BudgetResponseDto update(Long id, BudgetRequestDto sourceBudget) {
        Budget updatedBudget = getEntityById(id);
        budgetMapper.updateBudget(sourceBudget, updatedBudget);
        Budget updated = budgetRepository.update(updatedBudget);
        BudgetResponseDto responseDto = budgetMapper.toDto(updated);
        logger.log(Level.DEBUG, "Update budget: {}", responseDto);
        return responseDto;
    }

    @Override
    public boolean deleteById(Long id) {
        return budgetRepository.deleteById(id);
    }

    @Override
    public List<BudgetResponseDto> getAllBudgetsByUserId(Long userId) {
        List<Budget> budgets = budgetRepository.getAllByUserId(userId);
        List<BudgetResponseDto> responseDtoList = budgetMapper.toDto(budgets);
        logger.log(Level.DEBUG, "Get Budget by user id: {}", responseDtoList);
        return responseDtoList;
    }

    @Override
    public String getBudgetsExceededInfo(Long userId, Long categoryId) {
        Budget budget = getActiveBudgetByUserIdAndCategoryId(userId, categoryId);
        String info = "";
        if (budget != null) {
            TransactionFilter transactionFilter = new TransactionFilter(userId, null, budget.getPeriodStart(), budget.getPeriodEnd(), categoryId, TransactionType.EXPENSE);
            List<Transaction> transactions = transactionRepository.getAll(transactionFilter);
            BigDecimal sum = transactions.stream().map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (budget.getTotalAmount().compareTo(sum) >= 0) {
                info = MessageFormat
                        .format("#{0}: Перерасход для %name%! Ваши расходы: {1}, установленный бюджет: {2}",
                                budget.getId(), sum, budget.getTotalAmount());
                emailNotificationService.sendNotification(info);
            }
        }
        return info;
    }

    @Override
    public boolean deactivateBudget(Long id) {
        return budgetRepository.deactivateBudgetById(id);
    }

    private Budget getActiveBudgetByUserIdAndCategoryId(Long userId, Long categoryId) {
        return budgetRepository.getActiveBudgetByUserIdAndCategoryId(userId, categoryId).orElse(null);
    }
}
