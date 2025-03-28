package com.my.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.my.AbstractTestContainer;
import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.repository.impl.JdbcUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractTestContainer {
    private final JdbcUserRepository userRepository;
    private final UserController userController;
    private final ExceptionHandlerController exceptionHandlerController;
    private MockMvc mockMvc;

    @Autowired
    public UserControllerTest(JdbcUserRepository userRepository, UserController userController, ExceptionHandlerController exceptionHandlerController) {
        this.userRepository = userRepository;
        this.userController = userController;
        this.exceptionHandlerController = exceptionHandlerController;
    }

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(exceptionHandlerController)
                .build();
    }

    @AfterEach
    public void tearDown() {
        mockMvc = null;
    }

    @Test
    void whenGetAllUsers_thenReturnAllUsers() throws Exception {
        String actualResponse = mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<UserResponseDto> userResponses = objectMapper.readValue(actualResponse, new TypeReference<>() {
        });
        int count = userRepository.getAll().size();
        assertThat(userResponses).hasSize(count);
    }

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        var actualResponse = mockMvc.perform(get("/api/user/" + USER_ID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        actualResponse.setCharacterEncoding("UTF-8");
        UserResponseDto response = objectMapper.readValue(actualResponse.getContentAsString(), UserResponseDto.class);
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(USER_ID),
                () -> assertThat(response.getEmail()).isEqualTo(USER_EMAIL),
                () -> assertThat(response.getRole()).isEqualTo(USER_ROLE)
        );
    }

    @Test
    void whenGetUser_withWrongId_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/user/" + WRONG_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        UserRequestDto request = new UserRequestDto(NEW_USER_EMAIL + 1, NEW_USER_NAME + 1);
        var actualResponse = mockMvc.perform(patch("/api/user/" + USER_ID_FOR_UPDATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        UserResponseDto response = objectMapper.readValue(actualResponse.getContentAsString(), UserResponseDto.class);
        assertThat(response.getId()).isEqualTo(USER_ID_FOR_UPDATE);
        assertThat(response.getEmail()).isEqualTo(NEW_USER_EMAIL + 1);
    }

    @Test
    void whenUpdateUser_withSelfEmail_thenReturnUpdatedUser() throws Exception {
        UserRequestDto request = new UserRequestDto(USER_EMAIL, USER_NAME + 1);
        var actualResponse = mockMvc.perform(patch("/api/user/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        UserResponseDto response = objectMapper.readValue(actualResponse.getContentAsString(), UserResponseDto.class);
        assertThat(response.getId()).isEqualTo(USER_ID);
        assertThat(response.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    void whenUpdateUser_withWrongId_thenReturnNotFound() throws Exception {
        UserRequestDto request = new UserRequestDto(NEW_USER_EMAIL, NEW_USER_NAME);
        mockMvc.perform(patch("/api/user/" + WRONG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateUser_withWrongEmail_thenReturnBaRequest() throws Exception {
        UserRequestDto request = new UserRequestDto(USER_EMAIL, NEW_USER_NAME);
        mockMvc.perform(patch("/api/user/" + USER_ID_FOR_UPDATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDeleteUser_thenReturnOk() throws Exception {
        int count = userRepository.getAll().size();
        mockMvc.perform(delete("/api/user/" + USER_ID_FOR_DELETE))
                .andExpect(status().isOk());
        assertThat(userRepository.getAll()).hasSize(count - 1);
    }

    @Test
    void whenBlockUser_theReturnOk() throws Exception {
        assertThat(userRepository.getById(USER_ID_FOR_UPDATE).get().isBlocked()).isFalse();
        mockMvc.perform(put("/api/user/" + USER_ID_FOR_UPDATE + "/block"))
                .andExpect(status().isOk());
        assertThat(userRepository.getById(USER_ID_FOR_UPDATE).get().isBlocked()).isTrue();
    }

    @Test
    void whenUnblockUser_theReturnOk() throws Exception {
        assertThat(userRepository.getById(USER_ID_FOR_UNBLOCK).get().isBlocked()).isTrue();
        mockMvc.perform(put("/api/user/" + USER_ID_FOR_UNBLOCK + "/unblock"))
                .andExpect(status().isOk());
        assertThat(userRepository.getById(USER_ID_FOR_UNBLOCK).get().isBlocked()).isFalse();
    }
}
