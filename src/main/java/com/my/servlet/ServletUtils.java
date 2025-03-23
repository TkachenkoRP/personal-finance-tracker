package com.my.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.my.dto.ErrorResponseDto;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ServletUtils {
    private static final Logger logger = LogManager.getRootLogger();
    private final ObjectMapper objectMapper;

    public ServletUtils() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public boolean checkAuthentication(HttpServletResponse resp) {
        if (UserManager.isLoggedIn()) {
            return true;
        }
        handleAccessDenied(resp);
        return false;
    }

    public boolean checkAdminAccess(HttpServletResponse resp) {
        if (UserManager.isAdmin()) {
            return true;
        }
        handleAccessDenied(resp);
        return false;
    }

    public boolean checkAuthorizationToUpdate(HttpServletResponse resp, long id) {
        if (UserManager.isAdmin() || UserManager.getLoggedInUser().getId() == id) {
            return true;
        }
        handleAccessDenied(resp);
        return false;
    }

    public Optional<Long> getId(HttpServletRequest req) {
        String reqId = req.getParameter("id");
        if (reqId == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(reqId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getAction(HttpServletRequest req) {
        String action = req.getParameter("action");
        if (action == null) {
            return Optional.empty();
        }
        return Optional.of(action);
    }

    public TransactionFilter getTransactionFilter(HttpServletRequest req) {
        Long userId = getLongParameter(req, "userId");
        LocalDate date = getLocalDateParameter(req, "date");
        LocalDate from = getLocalDateParameter(req, "from");
        LocalDate to = getLocalDateParameter(req, "to");
        Long categoryId = getLongParameter(req, "categoryId");
        TransactionType type = getTransactionTypeParameter(req, "type");

        return new TransactionFilter(userId, date, from, to, categoryId, type);
    }

    private Long getLongParameter(HttpServletRequest req, String paramName) {
        String paramValue = req.getParameter(paramName);
        if (paramValue == null) {
            return null;
        }
        try {
            return Long.parseLong(paramValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate getLocalDateParameter(HttpServletRequest req, String paramName) {
        String paramValue = req.getParameter(paramName);
        if (paramValue == null) {
            return null;
        }
        try {
            return LocalDate.parse(paramValue, DateTimeFormatter.ofPattern("d.M.yyyy"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private TransactionType getTransactionTypeParameter(HttpServletRequest req, String paramName) {
        String paramValue = req.getParameter(paramName);
        if (paramValue == null) {
            return null;
        }
        try {
            return TransactionType.valueOf(paramValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public <T> void writeResponse(HttpServletResponse resp, T responseObject) throws IOException {
        resp.getWriter().write(objectMapper.writeValueAsString(responseObject));
    }

    public void handleError(HttpServletResponse resp, int status, String msg) {
        resp.setStatus(status);
        try {
            logger.log(Level.ERROR, "Error occurred: {}", msg);
            writeResponse(resp, new ErrorResponseDto(msg));
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Failed to write error response: {}", ex.getMessage());
        }
    }

    public void handleAccessDenied(HttpServletResponse resp) {
        handleError(resp, HttpServletResponse.SC_FORBIDDEN, "Access denied");
    }

    public <T> T readRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return objectMapper.readValue(req.getReader(), clazz);
    }

    public void setJsonContentType(HttpServletResponse resp) {
        resp.setContentType("application/json");
    }
}
