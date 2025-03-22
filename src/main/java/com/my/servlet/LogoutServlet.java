package com.my.servlet;

import com.my.service.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private final ServletUtils servletUtils;

    public LogoutServlet() {
        this.servletUtils = new ServletUtils();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
        UserManager.setLoggedInUser(null);
    }
}
