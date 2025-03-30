package com.my.controller;

import com.my.annotation.Audition;
import com.my.dto.BalanceResponseDto;
import com.my.dto.ExpenseAnalysisDto;
import com.my.dto.ExpensesResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.dto.IncomeResponseDto;
import com.my.dto.PeriodFilterRequestDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.model.TransactionFilter;
import com.my.service.TransactionService;
import com.my.service.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Audition
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponseDto> getAll(@ModelAttribute TransactionFilter filter) {
        return transactionService.getAll(filter);
    }

    @GetMapping("/{id}")
    public TransactionResponseDto getById(@PathVariable("id") Long id) {
        return transactionService.getById(id);
    }

    @GetMapping("/balance")
    public BalanceResponseDto getBalance() {
        return transactionService.getBalance(UserManager.getLoggedInUser().getId());
    }

    @GetMapping("/income")
    public IncomeResponseDto getIncome(@ModelAttribute PeriodFilterRequestDto filter) {
        return transactionService.getTotalIncome(UserManager.getLoggedInUser().getId(), filter.from(), filter.to());
    }

    @GetMapping("/total-expenses")
    public ExpensesResponseDto getTotalExpenses(@ModelAttribute PeriodFilterRequestDto filter) {
        return transactionService.getTotalExpenses(UserManager.getLoggedInUser().getId(), filter.from(), filter.to());
    }

    @GetMapping("/month-expenses")
    public ExpensesResponseDto getMonthExpenses() {
        return transactionService.getMonthExpense(UserManager.getLoggedInUser().getId());
    }

    @GetMapping("/analyze")
    public List<ExpenseAnalysisDto> getAnalyze(@ModelAttribute PeriodFilterRequestDto filter) {
        return transactionService.analyzeExpensesByCategory(UserManager.getLoggedInUser().getId(), filter.from(), filter.to());
    }

    @GetMapping("/report")
    public FullReportResponseDto getReport(@ModelAttribute PeriodFilterRequestDto filter) {
        return transactionService.generateFinancialReport(UserManager.getLoggedInUser().getId(), filter.from(), filter.to());
    }

    @PostMapping
    public TransactionResponseDto post(@RequestBody @Validated(TransactionRequestDto.Post.class) TransactionRequestDto request) {
        return transactionService.save(request);
    }

    @PatchMapping("/{id}")
    public TransactionResponseDto patch(@PathVariable("id") Long id, @RequestBody @Validated(TransactionRequestDto.Update.class) TransactionRequestDto request) {
        return transactionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        transactionService.deleteById(id);
    }
}
