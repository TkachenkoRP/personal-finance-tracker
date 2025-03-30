package com.my.service.impl;

import com.my.annotation.Loggable;
import com.my.dto.BalanceResponseDto;
import com.my.dto.ExpenseAnalysisDto;
import com.my.dto.ExpensesResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.dto.IncomeResponseDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.mapper.TransactionMapper;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.service.BudgetService;
import com.my.service.GoalService;
import com.my.service.TransactionService;
import com.my.service.UserManager;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

@Loggable
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LogManager.getRootLogger();
    private final JdbcTransactionRepository transactionRepository;
    private final BudgetService budgetService;
    private final GoalService goalService;
    private final TransactionMapper transactionMapper;

    private static final String TRANSACTION_NOT_FOUND = "Транзакция с id {0} не найдена";

    @Override
    public List<TransactionResponseDto> getAll(TransactionFilter filter) {
        List<Transaction> transactions = transactionRepository.getAll(filter);
        List<TransactionResponseDto> responseDtoList = transactionMapper.toDto(transactions);
        logger.log(Level.DEBUG, "Get all transactions");
        return responseDtoList;
    }

    @Override
    public Transaction getEntityById(Long id) {
        Transaction transaction = transactionRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(TRANSACTION_NOT_FOUND, id))
        );
        logger.log(Level.DEBUG, "Get entity transaction by id: {}", transaction);
        return transaction;
    }

    @Override
    public TransactionResponseDto getById(Long id) {
        Transaction transaction = getEntityById(id);
        TransactionResponseDto transactionResponseDto = transactionMapper.toDto(transaction);
        logger.log(Level.DEBUG, "Get transaction by id: {}", transactionResponseDto);
        return transactionResponseDto;
    }

    @Override
    public TransactionResponseDto save(TransactionRequestDto request) {
        Transaction requestEntity = transactionMapper.toEntity(request);
        requestEntity.setUserId(UserManager.getLoggedInUser().getId());
        Transaction saved = transactionRepository.save(requestEntity);
        TransactionResponseDto responseDto = transactionMapper.toDto(saved);
        processTransaction(saved);
        logger.log(Level.DEBUG, "Save transaction: {}", responseDto);
        return responseDto;
    }

    @Override
    public TransactionResponseDto update(Long id, TransactionRequestDto sourceTransaction) {
        Transaction updatedTransaction = getEntityById(id);
        transactionMapper.updateTransaction(sourceTransaction, updatedTransaction);
        Transaction updated = transactionRepository.update(updatedTransaction);
        TransactionResponseDto transactionResponseDto = transactionMapper.toDto(updated);
        logger.log(Level.DEBUG, "Update transaction: {}", transactionResponseDto);
        return transactionResponseDto;
    }

    @Override
    public boolean deleteById(Long id) {
        return transactionRepository.deleteById(id);
    }

    @Override
    public ExpensesResponseDto getMonthExpense(Long userId) {
        BigDecimal monthExpense = transactionRepository.getMonthExpense(userId);
        return new ExpensesResponseDto(monthExpense == null ? BigDecimal.ZERO : monthExpense);
    }

    @Override
    public BalanceResponseDto getBalance(Long userId) {
        BigDecimal balance = transactionRepository.getBalance(userId);
        return new BalanceResponseDto(balance);
    }

    @Override
    public IncomeResponseDto getTotalIncome(Long userId, LocalDate from, LocalDate to) {
        BigDecimal totalIncome = transactionRepository.getTotalIncome(userId, from, to);
        return new IncomeResponseDto(totalIncome);
    }

    @Override
    public ExpensesResponseDto getTotalExpenses(Long userId, LocalDate from, LocalDate to) {
        BigDecimal totalExpenses = transactionRepository.getTotalExpenses(userId, from, to);
        return new ExpensesResponseDto(totalExpenses);
    }

    @Override
    public List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) {
        List<ExpenseAnalysisDto> expenseAnalysisDtos = transactionRepository.analyzeExpensesByCategory(userId, from, to);
        logger.log(Level.DEBUG, "Get analyze: {}", expenseAnalysisDtos);
        return expenseAnalysisDtos;
    }

    @Override
    public FullReportResponseDto generateFinancialReport(Long userId, LocalDate from, LocalDate to) {
        IncomeResponseDto totalIncome = getTotalIncome(userId, from, to);
        ExpensesResponseDto totalExpenses = getTotalExpenses(userId, from, to);
        BalanceResponseDto totalBalance = new BalanceResponseDto(totalIncome.totalIncome().subtract(totalExpenses.totalExpenses()));
        FullReportResponseDto report = new FullReportResponseDto(totalIncome, totalExpenses, totalBalance);
        logger.log(Level.DEBUG, "Get report: {}", report);
        return report;
    }

    @Override
    public String processTransaction(Transaction transaction) {
        String msg;
        if (transaction.getType() == TransactionType.EXPENSE) {
            msg = budgetService.getBudgetsExceededInfo(transaction.getUserId(), transaction.getCategoryId()).getMessage();
        } else {
            msg = goalService.getGoalIncomeInfo(transaction.getUserId(), transaction.getCategoryId());
        }
        return msg;
    }
}