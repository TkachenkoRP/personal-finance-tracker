package com.my.servlet;

import com.my.AbstractTestContainer;
import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class LoginServletTest extends AbstractTestContainer {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);

    @Test
    void whenLogin_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("email")).thenReturn(USER_EMAIL);
        Mockito.when(request.getParameter("password")).thenReturn(USER_PASSWORD);
        loginServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(UserManager.getLoggedInUser().getId()).isEqualTo(USER_ID);
    }

    @Test
    void whenLogin_withUpperCaseEmail_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("email")).thenReturn(USER_EMAIL.toUpperCase());
        Mockito.when(request.getParameter("password")).thenReturn(USER_PASSWORD);
        loginServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(UserManager.getLoggedInUser().getId()).isEqualTo(USER_ID);
    }

    @Test
    void whenLogin_withWrongEmail_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("email")).thenReturn(USER_EMAIL + 1);
        Mockito.when(request.getParameter("password")).thenReturn(USER_PASSWORD);
        loginServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogin_withBlankEmail_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("password")).thenReturn(USER_PASSWORD);
        loginServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogin_withWrongPassword_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("email")).thenReturn(USER_EMAIL);
        Mockito.when(request.getParameter("password")).thenReturn(USER_PASSWORD + 1);
        loginServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogin_withBlankPassword_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("email")).thenReturn(USER_EMAIL);
        loginServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogin_withUpperCasePassword_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("email")).thenReturn(USER_EMAIL);
        Mockito.when(request.getParameter("password")).thenReturn(USER_PASSWORD.toUpperCase());
        loginServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }
}
