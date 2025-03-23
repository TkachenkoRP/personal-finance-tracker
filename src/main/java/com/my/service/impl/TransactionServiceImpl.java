package com.my.service.impl;

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
import com.my.repository.TransactionRepository;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.service.BudgetService;
import com.my.service.GoalService;
import com.my.service.TransactionService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LogManager.getRootLogger();
    private final JdbcTransactionRepository transactionRepository;
    private final BudgetService budgetService;
    private final GoalService goalService;

    private static final String TRANSACTION_NOT_FOUND = "Транзакция с id {0} не найдена";

    public TransactionServiceImpl() {
        this(new JdbcTransactionRepository(), new BudgetServiceImpl(), new GoalServiceImpl());
    }

    public TransactionServiceImpl(TransactionRepository transactionRepository, BudgetService budgetService, GoalService goalService) {
        this.transactionRepository = (JdbcTransactionRepository) transactionRepository;
        this.budgetService = budgetService;
        this.goalService = goalService;
    }

    @Override
    public List<TransactionResponseDto> getAll(TransactionFilter filter) throws SQLException {
        List<Transaction> transactions = transactionRepository.getAll(filter);
        List<TransactionResponseDto> responseDtoList = TransactionMapper.INSTANCE.toDto(transactions);
        logger.log(Level.DEBUG, "Get all transactions");
        return responseDtoList;
    }

    @Override
    public Transaction getEntityById(Long id) throws SQLException {
        Transaction transaction = transactionRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(TRANSACTION_NOT_FOUND, id))
        );
        logger.log(Level.DEBUG, "Get entity transaction by id: {}", transaction);
        return transaction;
    }

    @Override
    public TransactionResponseDto getById(Long id) throws SQLException {
        Transaction transaction = getEntityById(id);
        TransactionResponseDto transactionResponseDto = TransactionMapper.INSTANCE.toDto(transaction);
        logger.log(Level.DEBUG, "Get transaction by id: {}", transactionResponseDto);
        return transactionResponseDto;
    }

    @Override
    public TransactionResponseDto save(TransactionRequestDto request) throws SQLException {
        Transaction requestEntity = TransactionMapper.INSTANCE.toEntity(request);
        Transaction saved = transactionRepository.save(requestEntity);
        TransactionResponseDto responseDto = TransactionMapper.INSTANCE.toDto(saved);
        processTransaction(saved);
        logger.log(Level.DEBUG, "Save transaction: {}", responseDto);
        return responseDto;
    }

    @Override
    public TransactionResponseDto update(Long id, TransactionRequestDto sourceTransaction) throws SQLException {
        Transaction updatedTransaction = getEntityById(id);
        TransactionMapper.INSTANCE.updateTransaction(sourceTransaction, updatedTransaction);
        Transaction updated = transactionRepository.update(updatedTransaction);
        TransactionResponseDto transactionResponseDto = TransactionMapper.INSTANCE.toDto(updated);
        logger.log(Level.DEBUG, "Update transaction: {}", transactionResponseDto);
        return transactionResponseDto;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        return transactionRepository.deleteById(id);
    }

    @Override
    public ExpensesResponseDto getMonthExpense(Long userId) throws SQLException {
        BigDecimal monthExpense = transactionRepository.getMonthExpense(userId);
        return new ExpensesResponseDto(monthExpense);
    }

    @Override
    public BalanceResponseDto getBalance(Long userId) throws SQLException {
        BigDecimal balance = transactionRepository.getBalance(userId);
        return new BalanceResponseDto(balance);
    }

    @Override
    public IncomeResponseDto getTotalIncome(Long userId, LocalDate from, LocalDate to) throws SQLException {
        BigDecimal totalIncome = transactionRepository.getTotalIncome(userId, from, to);
        return new IncomeResponseDto(totalIncome);
    }

    @Override
    public ExpensesResponseDto getTotalExpenses(Long userId, LocalDate from, LocalDate to) throws SQLException {
        BigDecimal totalExpenses = transactionRepository.getTotalExpenses(userId, from, to);
        return new ExpensesResponseDto(totalExpenses);
    }

    @Override
    public List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) throws SQLException {
        List<ExpenseAnalysisDto> expenseAnalysisDtos = transactionRepository.analyzeExpensesByCategory(userId, from, to);
        logger.log(Level.DEBUG, "Get analyze: {}", expenseAnalysisDtos);
        return expenseAnalysisDtos;
    }

    @Override
    public FullReportResponseDto generateFinancialReport(Long userId, LocalDate from, LocalDate to) throws SQLException {
        IncomeResponseDto totalIncome = getTotalIncome(userId, from, to);
        ExpensesResponseDto totalExpenses = getTotalExpenses(userId, from, to);
        BalanceResponseDto totalBalance = new BalanceResponseDto(totalIncome.totalIncome().subtract(totalExpenses.totalExpenses()));
        FullReportResponseDto report = new FullReportResponseDto(totalIncome, totalExpenses, totalBalance);
        logger.log(Level.DEBUG, "Get report: {}", report);
        return report;
    }

    @Override
    public String processTransaction(Transaction transaction) throws SQLException {
        String msg;
        if (transaction.getType() == TransactionType.EXPENSE) {
            msg = budgetService.getBudgetsExceededInfo(transaction.getUserId(), transaction.getCategoryId());
        } else {
            msg = goalService.getGoalIncomeInfo(transaction.getUserId(), transaction.getCategoryId());
        }
        return msg;
    }
}