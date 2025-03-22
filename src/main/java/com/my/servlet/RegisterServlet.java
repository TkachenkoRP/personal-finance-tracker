package com.my.servlet;

import com.my.dto.UserRegisterRequestDto;
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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService;
    private final ServletUtils servletUtils;

    public RegisterServlet() {
        this.userService = new UserServiceImpl();
        this.servletUtils = new ServletUtils();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        try {
            if (req.getContentLength() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            save(req, resp);
        } catch (SQLException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        UserRegisterRequestDto userRegisterRequestDto = servletUtils.readRequestBody(req, UserRegisterRequestDto.class);
        User registeredUser = userService.registration(userRegisterRequestDto.getPassword(), userRegisterRequestDto.getName(), userRegisterRequestDto.getPassword());
        if (registeredUser == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        UserManager.setLoggedInUser(registeredUser);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        servletUtils.writeResponse(resp, UserMapper.INSTANCE.toDto(registeredUser));
    }
}
