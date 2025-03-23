package com.my.servlet;

import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.service.UserService;
import com.my.service.impl.UserServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private final UserService userService;
    private final ServletUtils servletUtils;

    public UserServlet() {
        this.userService = new UserServiceImpl();
        this.servletUtils = new ServletUtils();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        try {
            Optional<Long> id = servletUtils.getId(req);
            if (id.isEmpty()) {
                findAll(resp);
            } else {
                findById(resp, id.get());
            }
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }  catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void findAll(HttpServletResponse resp) throws SQLException, IOException {
        if (servletUtils.checkAdminAccess(resp)) {
            List<UserResponseDto> userResponses = userService.getAll();
            servletUtils.writeResponse(resp, userResponses);
        }
    }

    private void findById(HttpServletResponse resp, long id) throws SQLException, IOException {
        if (servletUtils.checkAdminAccess(resp)) {
            UserResponseDto userResponse = userService.getById(id);
            servletUtils.writeResponse(resp, userResponse);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        try {
            Optional<Long> idOptional = servletUtils.getId(req);
            if (idOptional.isEmpty()) {
                servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверно указан id");
                return;
            }
            long id = idOptional.get();
            String action = req.getParameter("action");
            if ("block".equals(action)) {
                block(resp, id);
            } else if ("unBlock".equals(action)) {
                unBlock(resp, id);
            } else {
                update(req, resp, id);
            }
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void update(HttpServletRequest req, HttpServletResponse resp, long id) throws IOException, SQLException {
        if (servletUtils.checkAuthorizationToUpdate(resp, id)) {
            UserRequestDto userRequest = servletUtils.readRequestBody(req, UserRequestDto.class);
            UserResponseDto updatedUser = userService.update(id, userRequest);
            servletUtils.writeResponse(resp, updatedUser);
        }
    }

    private void block(HttpServletResponse resp, long id) throws SQLException, IOException {
        if (servletUtils.checkAdminAccess(resp)) {
            boolean blocked = userService.blockUser(id);
            if (!blocked) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void unBlock(HttpServletResponse resp, long id) throws IOException, SQLException {
        if (servletUtils.checkAdminAccess(resp)) {
            boolean blocked = userService.unBlockUser(id);
            if (!blocked) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
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
        boolean deleted = userService.delete(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}