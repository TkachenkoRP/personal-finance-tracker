package com.my.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.my.AbstractTestContainer;
import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.repository.impl.JdbcGoalRepositoryImpl;
import com.my.service.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GoalControllerTest extends AbstractTestContainer {
    private final JdbcGoalRepositoryImpl goalRepository;
    private final GoalController goalController;
    private final ExceptionHandlerController exceptionHandlerController;

    @Autowired
    public GoalControllerTest(JdbcGoalRepositoryImpl goalRepository, GoalController goalController, ExceptionHandlerController exceptionHandlerController) {
        this.goalRepository = goalRepository;
        this.goalController = goalController;
        this.exceptionHandlerController = exceptionHandlerController;
    }

    @BeforeEach
    void beforeEach() {
        setUpMockMvc(goalController, exceptionHandlerController);
    }

    @Test
    void whenGetAllGoals_thenReturnAllGoals() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        int count = goalRepository.getAllByUserId(USER_ID).size();
        var actualResponse = performRequest(HttpMethod.GET, API_URL_GOAL, HttpStatus.OK);
        List<GoalResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).hasSize(count);
    }

    @Test
    void whenGetGoalById_thenReturnGoal() throws Exception {
        var actualResponse = performRequest(HttpMethod.GET, API_URL_GOAL + "/" + GOAL_ID, HttpStatus.OK);
        GoalResponseDto response = fromResponse(actualResponse, GoalResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(GOAL_ID),
                () -> assertThat(response.getCategory().getId()).isEqualTo(GOAL_CATEGORY_ID),
                () -> assertThat(response.getTargetAmount()).isEqualByComparingTo(GOAL_AMOUNT)
        );
    }

    @Test
    void whenGetGoalById_withWrongId_thenReturnNotFound() throws Exception {
        performRequest(HttpMethod.GET, API_URL_GOAL + "/" + WRONG_ID, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenAddNewGoal_thenReturnNewGoal() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        GoalRequestDto request = new GoalRequestDto(NEW_GOAL_AMOUNT, NEW_GOAL_CATEGORY_ID);
        var actualResponse = performRequest(HttpMethod.POST, API_URL_GOAL, request, HttpStatus.OK);
        GoalResponseDto response = fromResponse(actualResponse, GoalResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isNotNull(),
                () -> assertThat(response.getCategory().getId()).isEqualTo(NEW_GOAL_CATEGORY_ID),
                () -> assertThat(response.getTargetAmount()).isEqualByComparingTo(NEW_GOAL_AMOUNT),
                () -> assertThat(response.isActive()).isTrue()
        );
    }

    @Test
    void whenAddNewGoal_withWrongFields_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        performRequest(HttpMethod.POST, API_URL_GOAL, HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenUpdateGoal_thenReturnUpdatedGoal() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        GoalRequestDto request = new GoalRequestDto(GOAL_AMOUNT_FOR_UPDATE.add(GOAL_AMOUNT), null);
        var actualResponse = performRequest(HttpMethod.PATCH, API_URL_GOAL + "/" + GOAL_ID_FOR_UPDATE, request, HttpStatus.OK);
        GoalResponseDto response = fromResponse(actualResponse, GoalResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(GOAL_ID_FOR_UPDATE),
                () -> assertThat(response.getCategory().getId()).isEqualTo(GOAL_CATEGORY_ID_FOR_UPDATE),
                () -> assertThat(response.getTargetAmount()).isEqualByComparingTo(GOAL_AMOUNT_FOR_UPDATE.add(GOAL_AMOUNT))
        );
    }

    @Test
    void whenUpdateBudget_withWrongId_thenReturnNotFound() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        GoalRequestDto request = new GoalRequestDto(GOAL_AMOUNT_FOR_UPDATE.add(GOAL_AMOUNT), null);
        performRequest(HttpMethod.PATCH, API_URL_GOAL + "/" + WRONG_ID, request, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenDeleteBudgetById_thenReturnOk() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_DELETE));
        int count = goalRepository.getAll().size();
        performRequest(HttpMethod.DELETE, API_URL_GOAL + "/" + GOAL_ID_FOR_DELETE, HttpStatus.OK);

        assertThat(goalRepository.getAll()).hasSize(count - 1);
    }
}
