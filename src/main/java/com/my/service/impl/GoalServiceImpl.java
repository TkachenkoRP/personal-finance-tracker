package com.my.service.impl;

import com.my.annotation.Loggable;
import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.exception.AccessDeniedException;
import com.my.exception.EntityNotFoundException;
import com.my.mapper.GoalMapper;
import com.my.model.Goal;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.GoalRepository;
import com.my.repository.TransactionRepository;
import com.my.service.GoalService;
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
public class GoalServiceImpl implements GoalService {
    private static final Logger logger = LogManager.getRootLogger();
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService emailNotificationService;
    private final GoalMapper goalMapper;

    private static final String GOAL_NOT_FOUND = "Цель с id {0} не найдена";

    @Override
    public Goal getEntityById(Long id) {
        Goal goal = goalRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(GOAL_NOT_FOUND, id))
        );
        logger.log(Level.DEBUG, "Get entity goal by id: {}", goal);
        return goal;
    }

    @Override
    public GoalResponseDto getById(Long id) {
        Goal goal = getEntityById(id);
        GoalResponseDto responseDto = goalMapper.toDto(goal);
        logger.log(Level.DEBUG, "Get goal dto by id: {}", responseDto);
        return responseDto;
    }

    @Override
    public GoalResponseDto save(Long userId, GoalRequestDto goal) {
        if (userId == null) {
            throw new AccessDeniedException("У Вас нет права доступа");
        }
        Goal requestEntity = goalMapper.toEntity(goal);
        requestEntity.setActive(true);
        Goal saved = goalRepository.save(userId, requestEntity);
        GoalResponseDto responseDto = goalMapper.toDto(saved);
        logger.log(Level.DEBUG, "Save goal: {}", responseDto);
        return responseDto;
    }

    @Override
    public GoalResponseDto update(Long id, GoalRequestDto sourceGoal) {
        Goal updatedGoal = getEntityById(id);
        goalMapper.updateGoal(sourceGoal, updatedGoal);
        Goal updated = goalRepository.update(updatedGoal);
        GoalResponseDto responseDto = goalMapper.toDto(updated);
        logger.log(Level.DEBUG, "Update goal: {}", responseDto);
        return responseDto;
    }

    @Override
    public boolean deleteById(Long id) {
        return goalRepository.deleteById(id);
    }

    @Override
    public List<GoalResponseDto> getAllGoalsByUserId(Long userId) {
        List<Goal> goals = goalRepository.getAllByUserId(userId);
        List<GoalResponseDto> responseDtoList = goalMapper.toDto(goals);
        logger.log(Level.DEBUG, "Get goals by user id: {}", responseDtoList);
        return responseDtoList;
    }

    @Override
    public String getGoalIncomeInfo(Long userId, Long categoryId) {
        Goal goal = getAllActiveGoalByUserIdAndCategoryId(userId, categoryId);
        String info = null;
        if (goal != null) {
            TransactionFilter transactionFilter = new TransactionFilter(userId, null, null, null, categoryId, TransactionType.INCOME);
            List<Transaction> transactions = transactionRepository.getAll(transactionFilter);
            BigDecimal sum = transactions.stream().map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (goal.getTargetAmount().compareTo(sum) >= 0) {
                info = MessageFormat
                        .format("#{0}: Выполнена цель {1}, для %name%",
                                goal.getId(), goal.getTargetAmount());
                emailNotificationService.sendNotification(info);
            }
        }
        return info;
    }

    @Override
    public Goal getAllActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) {
        return goalRepository.getActiveGoalByUserIdAndCategoryId(userId, categoryId).orElse(null);
    }
}
