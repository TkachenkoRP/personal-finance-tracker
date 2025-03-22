package com.my.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.my.dto.ErrorResponseDto;
import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class ServletUtils {
    private static final Logger logger = LogManager.getRootLogger();

    private final ObjectMapper objectMapper;

    public ServletUtils() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public boolean checkAdminAccess(HttpServletResponse resp) throws IOException {
        if (!UserManager.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeResponse(resp, new ErrorResponseDto("Access denied"));
            return false;
        }
        return true;
    }

    public boolean checkAuthorizationToUpdate(HttpServletResponse resp, long id) throws IOException {
        if (!UserManager.isAdmin() && UserManager.getLoggedInUser().getId() != id) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeResponse(resp, new ErrorResponseDto("Access denied"));
            return false;
        }
        return true;
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

    public <T> void writeResponse(HttpServletResponse resp, T responseObject) throws IOException {
        resp.getWriter().write(objectMapper.writeValueAsString(responseObject));
    }

    public void handleError(HttpServletResponse resp, int status, String msg, Exception e) {
        resp.setStatus(status);
        try {
            logger.log(Level.ERROR, "Error occurred: ", e);
            writeResponse(resp, new ErrorResponseDto(msg));
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Failed to write error response: {}", ex.getMessage());
        }
    }

    public <T> T readRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return objectMapper.readValue(req.getReader(), clazz);
    }
}
