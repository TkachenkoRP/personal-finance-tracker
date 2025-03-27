package com.my.servlet;

import com.my.AbstractTestContainer;
import com.my.dto.ErrorResponseDto;
import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

class TransactionCategoryServletTest extends AbstractTestContainer {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);

    @Test
    void whenGetAllCategory_thenReturnAllCategory() throws Exception {
        int count = transactionCategoryRepository.getAll().size();
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        transactionCategoryServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        TransactionCategoryResponseDto[] categoryArray = objectMapper.readValue(result, TransactionCategoryResponseDto[].class);
        List<TransactionCategoryResponseDto> categoryResult = Arrays.asList(categoryArray);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(categoryResult).hasSize(count);
    }

    @Test
    void whenGetAllCategory_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        transactionCategoryServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenGetCategoryById_thenReturnCategory() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID.toString());
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        transactionCategoryServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        TransactionCategoryResponseDto categoryResult = objectMapper.readValue(result, TransactionCategoryResponseDto.class);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertAll(
                () -> assertThat(categoryResult).isNotNull(),
                () -> assertThat(categoryResult.getId()).isEqualTo(CATEGORY_ID),
                () -> assertThat(categoryResult.getCategoryName()).isEqualTo(CATEGORY_NAME)
        );
    }

    @Test
    void whenGetCategoryById_withWrongId_thenReturnNotFound() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        transactionCategoryServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenGetCategoryById_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID.toString());
        UserManager.setLoggedInUser(null);
        transactionCategoryServlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenAddCategory_thenReturnNewCategory() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(NEW_CATEGORY_NAME);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        int count = transactionCategoryRepository.getAll().size();
        transactionCategoryServlet.doPost(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        String result = stringWriter.getBuffer().toString().trim();
        TransactionCategoryResponseDto categoryResult = objectMapper.readValue(result, TransactionCategoryResponseDto.class);
        assertThat(categoryResult.getCategoryName()).isEqualTo(NEW_CATEGORY_NAME);
        assertThat(transactionCategoryRepository.getAll()).hasSize(count + 1);
    }

    @Test
    void whenAddCategory_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(NEW_CATEGORY_NAME + 1);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        int count = transactionCategoryRepository.getAll().size();
        transactionCategoryServlet.doPost(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
        assertThat(transactionCategoryRepository.getAll()).hasSize(count);
    }

    @Test
    void whenAddCategory_withWrongName_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(CATEGORY_NAME);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        int count = transactionCategoryRepository.getAll().size();
        transactionCategoryServlet.doPost(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
        assertThat(transactionCategoryRepository.getAll()).hasSize(count);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenAddCategory_withoutName_thenReturnBadRequest(String name) throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(name);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        int count = transactionCategoryRepository.getAll().size();
        transactionCategoryServlet.doPost(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
        assertThat(transactionCategoryRepository.getAll()).hasSize(count);
    }

    @Test
    void whenUpdateCategory_thenReturnUpdatedCategory() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID_FOR_UPDATE.toString());
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(CATEGORY_NAME_FOR_UPDATE + 1);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        int count = transactionCategoryRepository.getAll().size();
        transactionCategoryServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        String result = stringWriter.getBuffer().toString().trim();
        TransactionCategoryResponseDto categoryResult = objectMapper.readValue(result, TransactionCategoryResponseDto.class);
        assertThat(categoryResult.getCategoryName()).isEqualTo(CATEGORY_NAME_FOR_UPDATE + 1);
        assertThat(transactionCategoryRepository.getAll()).hasSize(count);
    }

    @Test
    void whenUpdateCategory_withWrongId_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(CATEGORY_NAME_FOR_UPDATE + 3);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        transactionCategoryServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenUpdateCategory_withEmptyId_thenReturnBadRequest(String id) throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(id);
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(CATEGORY_NAME_FOR_UPDATE + 3);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        transactionCategoryServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenUpdateCategory_withEmptyName_thenReturnBadRequest(String name) throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID_FOR_UPDATE.toString());
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(name);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        transactionCategoryServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenUpdateCategory_withWrongName_thenReturnBadRequest() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID_FOR_UPDATE.toString());
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(CATEGORY_NAME);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        int count = transactionCategoryRepository.getAll().size();
        transactionCategoryServlet.doPut(request, response);
        String result = stringWriter.getBuffer().toString().trim();
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
        assertThat(transactionCategoryRepository.getAll()).hasSize(count);
    }

    @Test
    void whenUpdateCategory_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID_FOR_UPDATE.toString());
        TransactionCategoryRequestDto transactionCategoryRequestDto = new TransactionCategoryRequestDto(CATEGORY_NAME_FOR_UPDATE + 2);
        BufferedReader reader = new BufferedReader(new StringReader(
                objectMapper.writeValueAsString(transactionCategoryRequestDto)
        ));
        Mockito.when(request.getReader()).thenReturn(reader);
        transactionCategoryServlet.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }

    @Test
    void whenDeleteCategory_thenReturnOk() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID_FOR_DELETE.toString());
        transactionCategoryServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThatThrownBy(() -> transactionCategoryService.getById(CATEGORY_ID_FOR_DELETE))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(CATEGORY_ID_FOR_DELETE.toString());
    }

    @Test
    void whenDeleteCategory_withWrongId_thenReturnNotFound() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        Mockito.when(request.getParameter("id")).thenReturn(WRONG_ID.toString());
        transactionCategoryServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void whenDeleteCategory_withBlankId_thenReturnNotFound() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(getAdminRole());
        Mockito.when(request.getParameter("id")).thenReturn("");
        transactionCategoryServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void whenDeleteCategory_withoutUser_thenReturnForbidden() throws Exception {
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        UserManager.setLoggedInUser(null);
        Mockito.when(request.getParameter("id")).thenReturn(CATEGORY_ID_FOR_UPDATE.toString());
        transactionCategoryServlet.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String result = stringWriter.getBuffer().toString().trim();
        ErrorResponseDto errorResponse = objectMapper.readValue(result, ErrorResponseDto.class);
        assertThat(errorResponse).isNotNull();
    }
}
