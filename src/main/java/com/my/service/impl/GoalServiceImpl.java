package com.my.service.impl;

import com.my.mapper.GoalMapper;
import com.my.model.Goal;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.GoalRepository;
import com.my.repository.TransactionRepository;
import com.my.service.GoalService;
import com.my.service.NotificationService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final NotificationService emailNotificationService;

    public GoalServiceImpl(GoalRepository goalRepository, TransactionRepository transactionRepository, NotificationService notificationService, NotificationService emailNotificationService) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public List<Goal> geAll() throws SQLException {
        return goalRepository.getAll();
    }

    @Override
    public Goal getById(Long id) throws SQLException {
        return goalRepository.getById(id).orElse(null);
    }

    @Override
    public Goal create(Long userId, Goal goal) throws SQLException {
        return goalRepository.save(userId, goal);
    }

    @Override
    public Goal update(Long id, Goal sourceGoal) throws SQLException {
        Goal updatedGoal = getById(id);
        if (updatedGoal == null) {
            return null;
        }

        GoalMapper.INSTANCE.updateTransaction(sourceGoal, updatedGoal);

        return goalRepository.update(updatedGoal);
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        return goalRepository.deleteById(id);
    }

    @Override
    public List<Goal> getAllGoalsByUserId(Long userId) throws SQLException {
        return goalRepository.getAllByUserId(userId);
    }

    @Override
    public String getGoalIncomeInfo(Long userId, Long categoryId) throws SQLException {
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
                notificationService.sendNotification(info);
                emailNotificationService.sendNotification(info);
            }
        }
        return info;
    }

    @Override
    public Goal getAllActiveGoalByUserIdAndCategoryId(Long userId, Long categoryId) throws SQLException {
        return goalRepository.getActiveGoalByUserIdAndCategoryId(userId, categoryId).orElse(null);
    }
}
