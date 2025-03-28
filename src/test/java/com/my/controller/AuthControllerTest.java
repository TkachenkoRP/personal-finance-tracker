package com.my.controller;

import com.my.AbstractTestContainer;
import com.my.dto.UserLoginRequestDto;
import com.my.dto.UserRegisterRequestDto;
import com.my.dto.UserResponseDto;
import com.my.repository.impl.JdbcUserRepository;
import com.my.service.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractTestContainer {

    private final JdbcUserRepository userRepository;
    private final AuthController authController;
    private final ExceptionHandlerController exceptionHandlerController;
    private MockMvc mockMvc;

    @Autowired
    public AuthControllerTest(JdbcUserRepository userRepository, AuthController authController, ExceptionHandlerController exceptionHandlerController) {
        this.userRepository = userRepository;
        this.authController = authController;
        this.exceptionHandlerController = exceptionHandlerController;
    }

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(exceptionHandlerController)
                .build();
    }

    @AfterEach
    public void tearDown() {
        mockMvc = null;
    }

    @Test
    void whenRegistrationUser_thenReturnNewUser() throws Exception {
        UserManager.setLoggedInUser(null);
        int count = userRepository.getAll().size();
        UserRegisterRequestDto request = new UserRegisterRequestDto(NEW_USER_EMAIL, NEW_USER_NAME, NEW_USER_PASSWORD);
        var actualResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        actualResponse.setCharacterEncoding("UTF-8");
        UserResponseDto response = objectMapper.readValue(actualResponse.getContentAsString(), UserResponseDto.class);
        assertAll(
                () -> assertThat(response.getEmail()).isEqualTo(NEW_USER_EMAIL),
                () -> assertThat(response.getRole()).isEqualTo(USER_ROLE),
                () -> assertThat(response.isBlocked()).isFalse(),
                () -> assertThat(UserManager.getLoggedInUser().getEmail()).isEqualTo(NEW_USER_EMAIL),
                () -> assertThat(UserManager.getLoggedInUser().getRole()).isEqualTo(USER_ROLE),
                () -> assertThat(userRepository.getAll()).hasSize(count + 1)
        );
    }

    @Test
    void whenRegistrationUser_withWrongEmail_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        int count = userRepository.getAll().size();
        UserRegisterRequestDto request = new UserRegisterRequestDto(USER_EMAIL, NEW_USER_NAME, NEW_USER_PASSWORD);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.getAll()).hasSize(count);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenRegistrationUser_withBlankEmail_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        int count = userRepository.getAll().size();
        UserRegisterRequestDto request = new UserRegisterRequestDto("", NEW_USER_NAME, NEW_USER_PASSWORD);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.getAll()).hasSize(count);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenRegistrationUser_withBlankPassword_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        int count = userRepository.getAll().size();
        UserRegisterRequestDto request = new UserRegisterRequestDto(NEW_USER_EMAIL, NEW_USER_NAME, "");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.getAll()).hasSize(count);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenRegistrationUser_withBlankName_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        int count = userRepository.getAll().size();
        UserRegisterRequestDto request = new UserRegisterRequestDto(NEW_USER_EMAIL, "", NEW_USER_PASSWORD);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.getAll()).hasSize(count);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenRegistrationUser_withBlankData_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        int count = userRepository.getAll().size();
        UserRegisterRequestDto request = new UserRegisterRequestDto("", "", "");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.getAll()).hasSize(count);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenRegistrationUser_withNullData_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        int count = userRepository.getAll().size();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.getAll()).hasSize(count);
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogin_withCorrectData_thenReturnUser() throws Exception {
        UserManager.setLoggedInUser(null);
        UserLoginRequestDto request = new UserLoginRequestDto(USER_EMAIL, USER_PASSWORD);
        var actualResponse = mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        actualResponse.setCharacterEncoding("UTF-8");
        UserResponseDto response = objectMapper.readValue(actualResponse.getContentAsString(), UserResponseDto.class);
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(USER_ID),
                () -> assertThat(response.getEmail()).isEqualTo(USER_EMAIL),
                () -> assertThat(response.getRole()).isEqualTo(USER_ROLE),
                () -> assertThat(UserManager.getLoggedInUser().getId()).isEqualTo(USER_ID),
                () -> assertThat(UserManager.getLoggedInUser().getEmail()).isEqualTo(USER_EMAIL),
                () -> assertThat(UserManager.getLoggedInUser().getRole()).isEqualTo(USER_ROLE)
        );
    }

    @Test
    void whenLogin_withCorrectDataUpperCaseEmail_thenReturnUser() throws Exception {
        UserManager.setLoggedInUser(null);
        UserLoginRequestDto request = new UserLoginRequestDto(USER_EMAIL.toUpperCase(), USER_PASSWORD);
        var actualResponse = mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        actualResponse.setCharacterEncoding("UTF-8");
        UserResponseDto response = objectMapper.readValue(actualResponse.getContentAsString(), UserResponseDto.class);
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(USER_ID),
                () -> assertThat(response.getEmail()).isEqualTo(USER_EMAIL),
                () -> assertThat(response.getRole()).isEqualTo(USER_ROLE),
                () -> assertThat(UserManager.getLoggedInUser().getId()).isEqualTo(USER_ID),
                () -> assertThat(UserManager.getLoggedInUser().getEmail()).isEqualTo(USER_EMAIL),
                () -> assertThat(UserManager.getLoggedInUser().getRole()).isEqualTo(USER_ROLE)
        );
    }

    @Test
    void whenLogin_withWrongEmail_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        UserLoginRequestDto request = new UserLoginRequestDto(USER_EMAIL + 1, USER_PASSWORD);
        mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogin_withWrongPassword_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        UserLoginRequestDto request = new UserLoginRequestDto(USER_EMAIL, USER_PASSWORD + 1);
        mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogin_withWrongUpperCasePassword_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(null);
        UserLoginRequestDto request = new UserLoginRequestDto(USER_EMAIL, USER_PASSWORD.toUpperCase());
        mockMvc.perform(get("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertThat(UserManager.getLoggedInUser()).isNull();
    }

    @Test
    void whenLogout_thenUserManagerNull() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        assertThat(UserManager.getLoggedInUser()).isNotNull();
        mockMvc.perform(get("/api/auth/logout"))
                .andExpect(status().isOk());
        assertThat(UserManager.getLoggedInUser()).isNull();
    }
}
