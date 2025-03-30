package com.my.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.my.AbstractTestContainer;
import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.repository.impl.JdbcBudgetRepositoryImpl;
import com.my.service.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class BudgetControllerTest extends AbstractTestContainer {
    private final JdbcBudgetRepositoryImpl budgetRepository;
    private final BudgetController budgetController;
    private final ExceptionHandlerController exceptionHandlerController;

    @Autowired
    public BudgetControllerTest(JdbcBudgetRepositoryImpl budgetRepository, BudgetController budgetController, ExceptionHandlerController exceptionHandlerController) {
        this.budgetRepository = budgetRepository;
        this.budgetController = budgetController;
        this.exceptionHandlerController = exceptionHandlerController;
    }

    @BeforeEach
    void beforeEach() {
        setUpMockMvc(budgetController, exceptionHandlerController);
    }

    @Test
    void whenGetAllBudgets_thenReturnAllBudgets() throws Exception {
        int count = budgetRepository.getAll().size();
        var actualResponse = performRequest(HttpMethod.GET, API_URL_BUDGET, HttpStatus.OK);
        List<BudgetResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).hasSize(count);
    }

    @Test
    void whenGetBudgetById_thenReturnBudget() throws Exception {
        var actualResponse = performRequest(HttpMethod.GET, API_URL_BUDGET + "/" + BUDGET_ID, HttpStatus.OK);
        BudgetResponseDto response = fromResponse(actualResponse, BudgetResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(BUDGET_ID),
                () -> assertThat(response.getCategory().getId()).isEqualTo(BUDGET_CATEGORY_ID),
                () -> assertThat(response.getTotalAmount()).isEqualByComparingTo(BUDGET_AMOUNT)
        );
    }

    @Test
    void whenGetBudgetById_withWrongId_thenReturnNotFound() throws Exception {
        performRequest(HttpMethod.GET, API_URL_BUDGET + "/" + WRONG_ID, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenAddNewBudget_thenReturnNewBudget() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        BudgetRequestDto request = new BudgetRequestDto(NEW_BUDGET_AMOUNT, NEW_BUDGET_DATE_FROM, NEW_BUDGET_DATE_TO, NEW_BUDGET_CATEGORY_ID);
        var actualResponse = performRequest(HttpMethod.POST, API_URL_BUDGET, request, HttpStatus.OK);
        BudgetResponseDto response = fromResponse(actualResponse, BudgetResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isNotNull(),
                () -> assertThat(response.getCategory().getId()).isEqualTo(NEW_BUDGET_CATEGORY_ID),
                () -> assertThat(response.getTotalAmount()).isEqualByComparingTo(NEW_BUDGET_AMOUNT)
        );
    }

    @Test
    void whenAddNewBudget_withWrongFields_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        int count = budgetRepository.getAll().size();
        performRequest(HttpMethod.POST, API_URL_BUDGET, HttpStatus.BAD_REQUEST);

        assertThat(budgetRepository.getAll()).hasSize(count);
    }

    @Test
    void whenUpdateBudget_thenReturnUpdatedBudget() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        BudgetRequestDto request = new BudgetRequestDto(BUDGET_AMOUNT_FOR_UPDATE.add(BUDGET_AMOUNT), null, null, null);
        var actualResponse = performRequest(HttpMethod.PATCH, API_URL_BUDGET + "/" + BUDGET_ID_FOR_UPDATE, request, HttpStatus.OK);
        BudgetResponseDto response = fromResponse(actualResponse, BudgetResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(BUDGET_ID_FOR_UPDATE),
                () -> assertThat(response.getCategory().getId()).isEqualTo(BUDGET_CATEGORY_ID_FOR_UPDATE),
                () -> assertThat(response.getTotalAmount()).isEqualByComparingTo(BUDGET_AMOUNT_FOR_UPDATE.add(BUDGET_AMOUNT))
        );
    }

    @Test
    void whenUpdateBudget_withWrongId_thenReturnNotFound() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        BudgetRequestDto request = new BudgetRequestDto(BUDGET_AMOUNT_FOR_UPDATE.add(BUDGET_AMOUNT), null, null, null);
        performRequest(HttpMethod.PATCH, API_URL_BUDGET + "/" + WRONG_ID, request, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenDeleteBudgetById_thenReturnOk() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        int count = budgetRepository.getAll().size();
        performRequest(HttpMethod.DELETE, API_URL_BUDGET + "/" + BUDGET_ID_FOR_DELETE, HttpStatus.OK);

        assertThat(budgetRepository.getAll()).hasSize(count - 1);
    }
}
