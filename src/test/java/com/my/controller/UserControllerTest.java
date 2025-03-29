package com.my.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.my.AbstractTestContainer;
import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.repository.impl.JdbcUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserControllerTest extends AbstractTestContainer {
    private final JdbcUserRepository userRepository;
    private final UserController userController;
    private final ExceptionHandlerController exceptionHandlerController;

    @Autowired
    public UserControllerTest(JdbcUserRepository userRepository, UserController userController, ExceptionHandlerController exceptionHandlerController) {
        this.userRepository = userRepository;
        this.userController = userController;
        this.exceptionHandlerController = exceptionHandlerController;
    }

    @BeforeEach
    public void setUpMockMvc() {
        setUpMockMvc(userController, exceptionHandlerController);
    }

    @Test
    void whenGetAllUsers_thenReturnAllUsers() throws Exception {
        var actualResponse = performRequest(HttpMethod.GET, API_URL_USER, HttpStatus.OK);
        List<UserResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });
        int count = userRepository.getAll().size();

        assertThat(response).hasSize(count);
    }

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        var actualResponse = performRequest(HttpMethod.GET, API_URL_USER + "/" + USER_ID, HttpStatus.OK);
        UserResponseDto response = fromResponse(actualResponse, UserResponseDto.class);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(USER_ID),
                () -> assertThat(response.getEmail()).isEqualTo(USER_EMAIL),
                () -> assertThat(response.getRole()).isEqualTo(USER_ROLE)
        );
    }

    @Test
    void whenGetUser_withWrongId_thenReturnNotFound() throws Exception {
        performRequest(HttpMethod.GET, API_URL_USER + "/" + WRONG_ID, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        UserRequestDto request = new UserRequestDto(NEW_USER_EMAIL + 1, NEW_USER_NAME + 1);
        var actualResponse = performRequest(HttpMethod.PATCH, API_URL_USER + "/" + USER_ID_FOR_UPDATE, request, HttpStatus.OK);
        UserResponseDto response = fromResponse(actualResponse, UserResponseDto.class);

        assertThat(response.getId()).isEqualTo(USER_ID_FOR_UPDATE);
        assertThat(response.getEmail()).isEqualTo(NEW_USER_EMAIL + 1);
    }

    @Test
    void whenUpdateUser_withSelfEmail_thenReturnUpdatedUser() throws Exception {
        UserRequestDto request = new UserRequestDto(USER_EMAIL, USER_NAME + 1);
        var actualResponse = performRequest(HttpMethod.PATCH, API_URL_USER + "/" + USER_ID, request, HttpStatus.OK);
        UserResponseDto response = fromResponse(actualResponse, UserResponseDto.class);

        assertThat(response.getId()).isEqualTo(USER_ID);
        assertThat(response.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    void whenUpdateUser_withWrongId_thenReturnNotFound() throws Exception {
        UserRequestDto request = new UserRequestDto(NEW_USER_EMAIL, NEW_USER_NAME);
        performRequest(HttpMethod.PATCH, API_URL_USER + "/" + WRONG_ID, request, HttpStatus.NOT_FOUND);

    }

    @Test
    void whenUpdateUser_withWrongEmail_thenReturnBaRequest() throws Exception {
        UserRequestDto request = new UserRequestDto(USER_EMAIL, NEW_USER_NAME);
        performRequest(HttpMethod.PATCH, API_URL_USER + "/" + USER_ID_FOR_UPDATE, request, HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenDeleteUser_thenReturnOk() throws Exception {
        int count = userRepository.getAll().size();
        performRequest(HttpMethod.DELETE, API_URL_USER + "/" + USER_ID_FOR_DELETE, HttpStatus.OK);

        assertThat(userRepository.getAll()).hasSize(count - 1);
    }

    @Test
    void whenBlockUser_theReturnOk() throws Exception {
        assertThat(userRepository.getById(USER_ID_FOR_UPDATE).get().isBlocked()).isFalse();
        performRequest(HttpMethod.PUT, API_URL_USER + "/" + USER_ID_FOR_UPDATE + "/block", HttpStatus.OK);

        assertThat(userRepository.getById(USER_ID_FOR_UPDATE).get().isBlocked()).isTrue();
    }

    @Test
    void whenUnblockUser_theReturnOk() throws Exception {
        assertThat(userRepository.getById(USER_ID_FOR_UNBLOCK).get().isBlocked()).isTrue();
        performRequest(HttpMethod.PUT, API_URL_USER + "/" + USER_ID_FOR_UNBLOCK + "/unblock", HttpStatus.OK);

        assertThat(userRepository.getById(USER_ID_FOR_UNBLOCK).get().isBlocked()).isFalse();
    }
}
