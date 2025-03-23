package com.my.servlet;

import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.dto.ReportResponseDto;
import com.my.exception.AccessDeniedException;
import com.my.exception.ArgumentNotValidException;
import com.my.exception.EntityNotFoundException;
import com.my.service.BudgetService;
import com.my.service.UserManager;
import com.my.service.impl.BudgetServiceImpl;
import com.my.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/budget")
public class BudgetServlet extends HttpServlet {
    private final ServletUtils servletUtils;
    private final BudgetService budgetService;

    public BudgetServlet() {
        this(new BudgetServiceImpl());
    }

    public BudgetServlet(BudgetService budgetService) {
        this.budgetService = budgetService;
        servletUtils = new ServletUtils();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        try {
            String action = req.getParameter("action");
            Optional<Long> optionalId = servletUtils.getId(req);
            if (optionalId.isEmpty()) {
                findAllByUserId(resp, UserManager.getLoggedInUser().getId());
                return;
            }
            long id = optionalId.get();
            if ("report".equals(action)) {
                getReport(resp, id);
            } else {
                findById(resp, id);
            }
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void getReport(HttpServletResponse resp, long id) throws SQLException, IOException {
        String budgetsExceededInfo = budgetService.getBudgetsExceededInfo(UserManager.getLoggedInUser().getId(), id);
        servletUtils.writeResponse(resp, new ReportResponseDto(budgetsExceededInfo));
    }

    private void findAllByUserId(HttpServletResponse resp, long userId) throws SQLException, IOException {
        List<BudgetResponseDto> allBudgetsByUserId = budgetService.getAllBudgetsByUserId(userId);
        servletUtils.writeResponse(resp, allBudgetsByUserId);
    }

    private void findById(HttpServletResponse resp, Long id) throws SQLException, IOException {
        BudgetResponseDto budget = budgetService.getById(id);
        servletUtils.writeResponse(resp, budget);
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
        } catch (AccessDeniedException e) {
            servletUtils.handleAccessDenied(resp);
        } catch (ArgumentNotValidException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, ArgumentNotValidException {
        BudgetRequestDto budgetRequestDto = servletUtils.readRequestBody(req, BudgetRequestDto.class);
        Validation.validationBudget(budgetRequestDto);
        BudgetResponseDto saved = budgetService.save(UserManager.getLoggedInUser().getId(), budgetRequestDto);
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

    private void update(HttpServletRequest req, HttpServletResponse resp, long id) throws IOException, SQLException {
        BudgetRequestDto budgetRequestDto = servletUtils.readRequestBody(req, BudgetRequestDto.class);
        Validation.validationBudget(budgetRequestDto);
        BudgetResponseDto updated = budgetService.update(id, budgetRequestDto);
        servletUtils.writeResponse(resp, updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
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
        boolean deleted = budgetService.deleteById(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
