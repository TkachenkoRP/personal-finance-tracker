package com.my.servlet;

import com.my.exception.UserException;
import com.my.mapper.UserMapper;
import com.my.model.User;
import com.my.service.UserManager;
import com.my.service.UserService;
import com.my.service.impl.UserServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService;
    private final ServletUtils servletUtils;

    public LoginServlet() {
        this.userService = new UserServiceImpl();
        this.servletUtils = new ServletUtils();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if (email == null || password == null) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Укажите email и password");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            login(email, password, resp);
        } catch (UserException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "An error occurred. Please try again later.");
        }
    }

    private void login(String email, String password, HttpServletResponse resp) throws SQLException, IOException {
        User user = userService.login(email, password);
        UserManager.setLoggedInUser(user);
        servletUtils.writeResponse(resp, UserMapper.INSTANCE.toDto(user));
    }
}
