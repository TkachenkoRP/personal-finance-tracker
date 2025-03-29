package com.my.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.my.AbstractTestContainer;
import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.repository.impl.JdbcTransactionCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TransactionCategoryControllerTest extends AbstractTestContainer {
    private final JdbcTransactionCategoryRepository transactionCategoryRepository;
    private final TransactionCategoryController transactionCategoryController;
    private final ExceptionHandlerController exceptionHandlerController;

    @Autowired
    public TransactionCategoryControllerTest(JdbcTransactionCategoryRepository transactionCategoryRepository, TransactionCategoryController transactionCategoryController, ExceptionHandlerController exceptionHandlerController) {
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.transactionCategoryController = transactionCategoryController;
        this.exceptionHandlerController = exceptionHandlerController;
    }

    @BeforeEach
    void beforeEach() {
        setUpMockMvc(transactionCategoryController, exceptionHandlerController);
    }

    @Test
    void whenGetAllCategory_thenReturnAllCategory() throws Exception {
        var actualResponse = performRequest(HttpMethod.GET, API_URL_CATEGORY, HttpStatus.OK);
        List<TransactionCategoryResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });
        int count = transactionCategoryRepository.getAll().size();

        assertThat(response).hasSize(count);
    }

    @Test
    void whenGetCategoryById_thenReturnCategory() throws Exception {
        var actualResponse = performRequest(HttpMethod.GET, API_URL_CATEGORY + "/" + CATEGORY_ID, HttpStatus.OK);
        TransactionCategoryResponseDto response = fromResponse(actualResponse, TransactionCategoryResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(CATEGORY_ID),
                () -> assertThat(response.getCategoryName()).isEqualTo(CATEGORY_NAME)
        );
    }

    @Test
    void whenGetCategoryById_withWrongId_thenReturnNotFound() throws Exception {
        performRequest(HttpMethod.GET, API_URL_CATEGORY + "/" + WRONG_ID, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenAddNewCategory_thenReturnNewCategory() throws Exception {
        int count = transactionCategoryRepository.getAll().size();
        TransactionCategoryRequestDto request = new TransactionCategoryRequestDto(NEW_CATEGORY_NAME);
        var actualResponse = performRequest(HttpMethod.POST, API_URL_CATEGORY, request, HttpStatus.OK);
        TransactionCategoryResponseDto response = fromResponse(actualResponse, TransactionCategoryResponseDto.class);

        assertThat(transactionCategoryRepository.getAll()).hasSize(count + 1);
        assertThat(response.getCategoryName()).isEqualTo(NEW_CATEGORY_NAME);
    }

    @Test
    void whenAddNewCategory_withWrongName_thenReturnBadRequest() throws Exception {
        int count = transactionCategoryRepository.getAll().size();
        TransactionCategoryRequestDto request = new TransactionCategoryRequestDto(CATEGORY_NAME);
        performRequest(HttpMethod.POST, API_URL_CATEGORY, request, HttpStatus.BAD_REQUEST);
        assertThat(transactionCategoryRepository.getAll()).hasSize(count);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenAddCategory_withoutName_thenReturnBadRequest(String name) throws Exception {
        TransactionCategoryRequestDto request = new TransactionCategoryRequestDto(name);
        performRequest(HttpMethod.POST, API_URL_CATEGORY, request, HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenUpdateCategory_thenReturnUpdatedCategory() throws Exception {
        TransactionCategoryRequestDto request = new TransactionCategoryRequestDto(NEW_CATEGORY_NAME + 1);
        var actualResponse = performRequest(HttpMethod.PATCH, API_URL_CATEGORY + "/" + CATEGORY_ID_FOR_UPDATE, request, HttpStatus.OK);
        TransactionCategoryResponseDto response = fromResponse(actualResponse, TransactionCategoryResponseDto.class);

        assertThat(response.getId()).isEqualTo(CATEGORY_ID_FOR_UPDATE);
        assertThat(response.getCategoryName()).isEqualTo(NEW_CATEGORY_NAME + 1);
    }

    @Test
    void whenUpdateCategory_withWrongId_thenReturnNotFound() throws Exception {
        TransactionCategoryRequestDto request = new TransactionCategoryRequestDto(NEW_CATEGORY_NAME + 2);
        performRequest(HttpMethod.PATCH, API_URL_CATEGORY + "/" + WRONG_ID, request, HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void whenUpdateCategory_withoutName_thenReturnBadRequest(String name) throws Exception {
        TransactionCategoryRequestDto request = new TransactionCategoryRequestDto(name);
        performRequest(HttpMethod.PATCH, API_URL_CATEGORY + "/" + CATEGORY_ID_FOR_UPDATE, request, HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenUpdateCategory_withWrongName_thenReturnBadRequest() throws Exception {
        TransactionCategoryRequestDto request = new TransactionCategoryRequestDto(CATEGORY_NAME);
        performRequest(HttpMethod.PATCH, API_URL_CATEGORY + "/" + CATEGORY_ID_FOR_UPDATE, request, HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenDeleteCategory_thenReturnOk() throws Exception {
        int count = transactionCategoryRepository.getAll().size();
        performRequest(HttpMethod.DELETE, API_URL_CATEGORY + "/" + CATEGORY_ID_FOR_DELETE, HttpStatus.OK);

        assertThat(transactionCategoryRepository.getAll()).hasSize(count - 1);
    }
}
