package com.my.servlet;

import com.my.annotation.Audition;
import com.my.annotation.Loggable;
import com.my.dto.BalanceResponseDto;
import com.my.dto.ExpenseAnalysisDto;
import com.my.dto.ExpensesResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.dto.IncomeResponseDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.exception.ArgumentNotValidException;
import com.my.exception.EntityNotFoundException;
import com.my.model.TransactionFilter;
import com.my.service.TransactionService;
import com.my.service.UserManager;
import com.my.service.impl.TransactionServiceImpl;
import com.my.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Loggable
@Audition
@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {
    private final ServletUtils servletUtils;
    private final TransactionService transactionService;

    public TransactionServlet() {
        this(new TransactionServiceImpl());
    }

    public TransactionServlet(TransactionService transactionService) {
        this.transactionService = transactionService;
        this.servletUtils = new ServletUtils();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        TransactionFilter filter = servletUtils.getTransactionFilter(req);
        String action = servletUtils.getAction(req).orElse("");
        Optional<Long> id = servletUtils.getId(req);
        try {
            switch (action) {
                case "balance":
                    getBalance(resp);
                    break;
                case "income":
                    getIncome(resp, filter.from(), filter.to());
                    break;
                case "total-expenses":
                    getTotalExpenses(resp, filter.from(), filter.to());
                    break;
                case "month-expenses":
                    getMonthExpenses(resp);
                    break;
                case "analyze":
                    getAnalyze(resp, filter.from(), filter.to());
                    break;
                case "report":
                    getReport(resp, filter.from(), filter.to());
                    break;
                default:
                    if (id.isEmpty()) {
                        findAll(resp, filter);
                    } else {
                        findById(resp, id.get());
                    }
                    break;
            }
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void getReport(HttpServletResponse resp, LocalDate from, LocalDate to) throws SQLException, IOException {
        FullReportResponseDto fullReportResponseDto = transactionService.generateFinancialReport(UserManager.getLoggedInUser().getId(), from, to);
        servletUtils.writeResponse(resp, fullReportResponseDto);
    }


    private void getAnalyze(HttpServletResponse resp, LocalDate from, LocalDate to) throws SQLException, IOException {
        List<ExpenseAnalysisDto> expenseAnalysisDtos = transactionService.analyzeExpensesByCategory(UserManager.getLoggedInUser().getId(), from, to);
        servletUtils.writeResponse(resp, expenseAnalysisDtos);
    }


    private void getMonthExpenses(HttpServletResponse resp) throws SQLException, IOException {
        ExpensesResponseDto monthExpense = transactionService.getMonthExpense(UserManager.getLoggedInUser().getId());
        servletUtils.writeResponse(resp, monthExpense);
    }

    private void getTotalExpenses(HttpServletResponse resp, LocalDate from, LocalDate to) throws IOException, SQLException {
        ExpensesResponseDto totalExpenses = transactionService.getTotalExpenses(UserManager.getLoggedInUser().getId(), from, to);
        servletUtils.writeResponse(resp, totalExpenses);
    }


    private void getIncome(HttpServletResponse resp, LocalDate from, LocalDate to) throws SQLException, IOException {
        IncomeResponseDto totalIncome = transactionService.getTotalIncome(UserManager.getLoggedInUser().getId(), from, to);
        servletUtils.writeResponse(resp, totalIncome);
    }


    private void getBalance(HttpServletResponse resp) throws SQLException, IOException {
        BalanceResponseDto balance = transactionService.getBalance(UserManager.getLoggedInUser().getId());
        servletUtils.writeResponse(resp, balance);
    }

    private void findAll(HttpServletResponse resp, TransactionFilter filter) throws IOException, SQLException {
        List<TransactionResponseDto> transactions;
        if (UserManager.isAdmin()) {
            transactions = transactionService.getAll(filter);
        } else {
            transactions = transactionService.getAll(filter.withUserId(UserManager.getLoggedInUser().getId()));
        }
        servletUtils.writeResponse(resp, transactions);
    }

    private void findById(HttpServletResponse resp, Long id) throws SQLException, IOException {
        TransactionResponseDto transaction = transactionService.getById(id);
        servletUtils.writeResponse(resp, transaction);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        int contentLength = req.getContentLength();
        if (contentLength <= 0) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Тело запроса пустое");
            return;
        }
        try {
            save(req, resp);
        } catch (ArgumentNotValidException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }  catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during saving");
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        TransactionRequestDto transactionRequestDto = servletUtils.readRequestBody(req, TransactionRequestDto.class);
        Validation.validationTransaction(transactionRequestDto);
        TransactionResponseDto saved = transactionService.save(transactionRequestDto);
        servletUtils.writeResponse(resp, saved);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        try {
            Optional<Long> id = servletUtils.getId(req);
            if (id.isEmpty()) {
                servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверно указан id");
                return;
            }
            update(req, resp, id.get());
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ArgumentNotValidException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }  catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void update(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException, SQLException {
        if (!servletUtils.checkAuthorizationToUpdate(resp, id)) {
            return;
        }
        TransactionRequestDto transactionRequestDto = servletUtils.readRequestBody(req, TransactionRequestDto.class);
        Validation.validationTransaction(transactionRequestDto);
        TransactionResponseDto updated = transactionService.update(id, transactionRequestDto);
        servletUtils.writeResponse(resp, updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        try {
            Optional<Long> id = servletUtils.getId(req);
            if (id.isEmpty()) {
                servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверно указан id");
                return;
            }
            delete(resp, id.get());
        } catch (SQLException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void delete(HttpServletResponse resp, Long id) throws SQLException {
        if (!servletUtils.checkAuthorizationToUpdate(resp, id)) {
            return;
        }
        boolean deleted = transactionService.deleteById(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}