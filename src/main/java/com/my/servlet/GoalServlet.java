package com.my.servlet;

import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.exception.AccessDeniedException;
import com.my.exception.ArgumentNotValidException;
import com.my.exception.EntityNotFoundException;
import com.my.service.GoalService;
import com.my.service.UserManager;
import com.my.service.impl.GoalServiceImpl;
import com.my.util.Validation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/goal")
public class GoalServlet extends HttpServlet {
    private final ServletUtils servletUtils;
    private final GoalService goalService;

    public GoalServlet() {
        this(new GoalServiceImpl());
    }

    public GoalServlet(GoalService goalService) {
        this.goalService = goalService;
        this.servletUtils = new ServletUtils();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        try {
            Optional<Long> optionalId = servletUtils.getId(req);
            if (optionalId.isEmpty()) {
                findAllByUserId(resp, UserManager.getLoggedInUser().getId());
                return;
            }
            long id = optionalId.get();
            findById(resp, id);
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage(), null);
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.", e);
        }
    }

    private void findAllByUserId(HttpServletResponse resp, Long id) throws SQLException, IOException {
        List<GoalResponseDto> goals = goalService.getAllGoalsByUserId(id);
        servletUtils.writeResponse(resp, goals);
    }

    private void findById(HttpServletResponse resp, long id) throws SQLException, IOException {
        GoalResponseDto goal = goalService.getById(id);
        servletUtils.writeResponse(resp, goal);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        int contentLength = req.getContentLength();
        if (contentLength <= 0) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Тело запроса пустое", null);
            return;
        }
        try {
            save(req, resp);
        } catch (AccessDeniedException e) {
            servletUtils.handleAccessDenied(resp);
        } catch (ArgumentNotValidException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), e);
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.", e);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        GoalRequestDto goalRequestDto = servletUtils.readRequestBody(req, GoalRequestDto.class);
        Validation.validationGoal(goalRequestDto);
        GoalResponseDto saved = goalService.save(UserManager.getLoggedInUser().getId(), goalRequestDto);
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
                servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверно указан id", null);
                return;
            }
            update(req, resp, id.get());
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage(), null);
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.", e);
        }
    }

    private void update(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException, SQLException {
        GoalRequestDto goalRequestDto = servletUtils.readRequestBody(req, GoalRequestDto.class);
        GoalResponseDto updated = goalService.update(id, goalRequestDto);
        servletUtils.writeResponse(resp, updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        try {
            Optional<Long> id = servletUtils.getId(req);
            if (id.isEmpty()) {
                servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверно указан id", null);
                return;
            }
            delete(resp, id.get());
        } catch (SQLException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.", e);
        }
    }

    private void delete(HttpServletResponse resp, Long id) throws SQLException {
        boolean deleted = goalService.deleteById(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
