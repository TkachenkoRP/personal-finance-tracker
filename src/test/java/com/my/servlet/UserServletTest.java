package com.my.servlet;

import com.my.AbstractTestContainer;
import com.my.dto.ErrorResponseDto;
import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserServletTest extends AbstractTestContainer {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);

    @Test
    void whenGetAllUsers_thenReturnAllUsers() throws Exception {
        int count = userService.getAll().size();
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        userServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        UserResponseDto[] userArray = objectMapper.readValue(result, UserResponseDto[].class);
        List<UserResponseDto> usersResult = Arrays.asList(userArray);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(usersResult).hasSize(count);
    }

    @Test
    void whenGetAllUsers_withWrongRole_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        userServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenGetAllUsers_withoutUser_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        userServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID.toString());
        UserManager.setLoggedInUser(getAdminRole());
        userServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        UserResponseDto userResult = objectMapper.readValue(result, UserResponseDto.class);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertAll(
                () -> assertThat(userResult).isNotNull(),
                () -> assertThat(userResult.getId()).isEqualTo(USER_ID),
                () -> assertThat(userResult.getEmail()).isEqualTo(USER_EMAIL),
                () -> assertThat(userResult.getName()).isEqualTo(USER_NAME),
                () -> assertThat(userResult.getRole()).isEqualTo(USER_ROLE)
        );
    }

    @Test
    void whenGetUserById_withWrongId_thenNotFound() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        UserManager.setLoggedInUser(getAdminRole());
        userServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenGetUserById_withWrongRole_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID.toString());
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        userServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenGetUserById_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID.toString());
        UserManager.setLoggedInUser(null);
        userServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUpdateUser_withAdminRole_thenReturnUpdatedUser() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        UserResponseDto userForUpdate = userService.getById(USER_ID_FOR_UPDATE);
        assertThat(userForUpdate.getId()).isEqualTo(USER_ID_FOR_UPDATE);
        UserRequestDto userRequest = new UserRequestDto(NEW_USER_EMAIL + 1, NEW_USER_NAME + 1);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        String result = stringWriter.getBuffer().toString().trim();
        UserResponseDto userResult = objectMapper.readValue(result, UserResponseDto.class);
        assertAll(
                () -> assertThat(userResult.getId()).isEqualTo(USER_ID_FOR_UPDATE),
                () -> assertThat(userResult.getEmail()).isEqualTo(NEW_USER_EMAIL + 1),
                () -> assertThat(userResult.getEmail()).isNotEqualTo(userForUpdate.getEmail()),
                () -> assertThat(userResult.getName()).isEqualTo(NEW_USER_NAME + 1),
                () -> assertThat(userResult.getName()).isNotEqualTo(userForUpdate.getName())
        );
    }

    @Test
    void whenUpdateUser_withSelfUser_thenReturnUpdatedUser() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_UPDATE));
        UserResponseDto userForUpdate = userService.getById(USER_ID_FOR_UPDATE);
        assertThat(userForUpdate.getId()).isEqualTo(USER_ID_FOR_UPDATE);
        UserRequestDto userRequest = new UserRequestDto(NEW_USER_EMAIL + 2, NEW_USER_NAME + 2);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        String result = stringWriter.getBuffer().toString().trim();
        UserResponseDto userResult = objectMapper.readValue(result, UserResponseDto.class);
        assertAll(
                () -> assertThat(userResult.getId()).isEqualTo(USER_ID_FOR_UPDATE),
                () -> assertThat(userResult.getEmail()).isEqualTo(NEW_USER_EMAIL + 2),
                () -> assertThat(userResult.getEmail()).isNotEqualTo(userForUpdate.getEmail()),
                () -> assertThat(userResult.getName()).isEqualTo(NEW_USER_NAME + 2),
                () -> assertThat(userResult.getName()).isNotEqualTo(userForUpdate.getName())
        );
    }

    @Test
    void whenUpdateUser_withAdminRoleAndSelfEmail_thenReturnUpdatedUser() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        UserResponseDto userForUpdate = userService.getById(USER_ID_FOR_UPDATE);
        assertThat(userForUpdate.getId()).isEqualTo(USER_ID_FOR_UPDATE);
        UserRequestDto userRequest = new UserRequestDto(userForUpdate.getEmail(), NEW_USER_NAME + 3);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        String result = stringWriter.getBuffer().toString().trim();
        UserResponseDto userResult = objectMapper.readValue(result, UserResponseDto.class);
        assertAll(
                () -> assertThat(userResult.getId()).isEqualTo(USER_ID_FOR_UPDATE),
                () -> assertThat(userResult.getEmail()).isEqualTo(userForUpdate.getEmail()),
                () -> assertThat(userResult.getName()).isEqualTo(NEW_USER_NAME + 3),
                () -> assertThat(userResult.getName()).isNotEqualTo(userForUpdate.getName())
        );
    }

    @Test
    void whenUpdateUser_withWrongId_thenReturnNotFound() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        UserRequestDto userRequest = new UserRequestDto(NEW_USER_EMAIL + 1, NEW_USER_NAME + 1);
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUpdateUser_withBlankId_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        UserRequestDto userRequest = new UserRequestDto(NEW_USER_EMAIL + 1, NEW_USER_NAME + 1);
        Mockito.when(request.getParameter("id")).thenReturn("");
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUpdateUser_withWrongUserRole_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        UserRequestDto userRequest = new UserRequestDto(NEW_USER_EMAIL + 1, NEW_USER_NAME + 1);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUpdateUser_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        UserRequestDto userRequest = new UserRequestDto(NEW_USER_EMAIL + 1, NEW_USER_NAME + 1);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUpdateUser_withWrongEmail_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        UserRequestDto userRequest = new UserRequestDto(USER_EMAIL, NEW_USER_NAME);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(userRequest)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenBlockUser_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        UserResponseDto userForBlock = userService.getById(USER_ID);
        assertThat(userForBlock.getId()).isEqualTo(USER_ID);
        assertThat(userForBlock.isBlocked()).isFalse();
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID.toString());
        Mockito.when(request.getParameter("action")).thenReturn("block");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        UserResponseDto userResult = userService.getById(USER_ID);
        assertAll(
                () -> assertThat(userResult.getId()).isEqualTo(USER_ID),
                () -> assertThat(userResult.isBlocked()).isTrue()
        );
    }

    @Test
    void whenBlockUser_withWrongUserRole_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID.toString());
        Mockito.when(request.getParameter("action")).thenReturn("block");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenBlockUser_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID.toString());
        Mockito.when(request.getParameter("action")).thenReturn("block");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenBlockUser_withWrongId_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        Mockito.when(request.getParameter("action")).thenReturn("unblock");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void whenUnblockUser_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        UserResponseDto userForBlock = userService.getById(USER_ID_FOR_UNBLOCK);
        assertThat(userForBlock.getId()).isEqualTo(USER_ID_FOR_UNBLOCK);
        assertThat(userForBlock.isBlocked()).isTrue();
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UNBLOCK.toString());
        Mockito.when(request.getParameter("action")).thenReturn("unblock");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        UserResponseDto userResult = userService.getById(USER_ID_FOR_UNBLOCK);
        assertAll(
                () -> assertThat(userResult.getId()).isEqualTo(USER_ID_FOR_UNBLOCK),
                () -> assertThat(userResult.isBlocked()).isFalse()
        );
    }

    @Test
    void whenUnblockUser_withWrongUserRole_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UNBLOCK.toString());
        Mockito.when(request.getParameter("action")).thenReturn("unblock");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUnblockUser_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UNBLOCK.toString());
        Mockito.when(request.getParameter("action")).thenReturn("unblock");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUnblockUser_withWrongId_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        Mockito.when(request.getParameter("action")).thenReturn("block");
        userServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void whenDeleteUser_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_DELETE.toString());
        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThatThrownBy(() -> userService.getById(USER_ID_FOR_DELETE))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(USER_ID_FOR_DELETE.toString());
    }

    @Test
    void whenDeleteUser_withWrongId_thenReturnNotFound() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void whenDeleteUser_withBlankId_thenReturnNotFound() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        Mockito.when(request.getParameter("id")).thenReturn("");
        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenDeleteUser_withWrongRole_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenDeleteUser_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("id")).thenReturn(USER_ID_FOR_UPDATE.toString());
        userServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }
}
