package com.my.servlet;

import com.my.AbstractTestContainer;
import com.my.model.User;
import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class LogoutServletTest extends AbstractTestContainer {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    @Test
    void whenLogout_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(new User());
        assertThat(UserManager.getLoggedInUser()).isNotNull();
        logoutServlet.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }
}
