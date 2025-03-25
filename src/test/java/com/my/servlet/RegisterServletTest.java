package com.my.servlet;

import com.my.AbstractTestContainer;
import com.my.dto.UserRegisterRequestDto;
import com.my.model.UserRole;
import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RegisterServletTest extends AbstractTestContainer {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);

    @Test
    void whenRegistrationUser_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto(NEW_USER_EMAIL, NEW_USER_NAME, NEW_USER_PASSWORD);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);

        assertAll(
                () -> assertThat(UserManager.getLoggedInUser()).isNotNull(),
                () -> assertThat(UserManager.getLoggedInUser().getId()).isNotNull(),
                () -> assertThat(UserManager.getLoggedInUser().getName()).isEqualTo(NEW_USER_NAME),
                () -> assertThat(UserManager.getLoggedInUser().getRole()).isEqualTo(UserRole.ROLE_USER),
                () -> assertThat(UserManager.getLoggedInUser().isBlocked()).isFalse(),
                () -> assertThat(userRepository.getAll()).hasSize(count + 1)
        );
    }


    @Test
    void whenRegistrationUser_withWrongEmail_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto(USER_EMAIL, NEW_USER_NAME, NEW_USER_PASSWORD);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenRegistrationUser_withNullEmail_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto(null, NEW_USER_NAME, NEW_USER_PASSWORD);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenRegistrationUser_withBlankEmail_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto("", NEW_USER_NAME, NEW_USER_PASSWORD);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenRegistrationUser_withNullName_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto(USER_EMAIL, null, NEW_USER_PASSWORD);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenRegistrationUser_withBlankName_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto(USER_EMAIL, "", NEW_USER_PASSWORD);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenRegistrationUser_withNullPassword_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto(USER_EMAIL, NEW_USER_NAME, null);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenRegistrationUser_withBlankPassword_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        int count = userRepository.getAll().size();
        UserManager.setLoggedInUser(null);
        UserRegisterRequestDto userRegisterRequest = new UserRegisterRequestDto(USER_EMAIL, NEW_USER_NAME, "");
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRegisterRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        registerServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(UserManager.getLoggedInUser()).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
    }
}
