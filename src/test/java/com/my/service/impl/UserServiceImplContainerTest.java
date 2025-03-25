package com.my.service.impl;

import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.UserException;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.AbstractTestContainer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceImplContainerTest extends AbstractTestContainer {
    final Long adminId = 1L;
    final String adminEmail = "admin@admin";
    final String adminPassword = "aDmIn";
    final String userEmail = "user@user";
    final String userName = "user";
    final String userPassword = "user";
    final Long idForUpdate = 3L;
    final String newUsername = "NewUserName";
    UserResponseDto userTest;
    final Long wrongId = 100L;
    final Long newId = 6L;
    final Long idForDelete = 4L;

    @Test
    void whenGetAllUsers_thenReturnAllUsers() throws Exception {
        int count = userRepository.getAll().size();
        List<UserResponseDto> userList = userService.getAll();
        assertThat(userList).hasSize(count);
    }

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        UserResponseDto foundUser = userService.getById(adminId);
        assertThat(foundUser.getId()).isEqualTo(adminId);
        assertThat(foundUser.getEmail()).isEqualTo(adminEmail);
    }

    @Test
    void whenGetUserById_withWrongId_thenReturnNull() {
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> userService.getById(wrongId));
        assertThat(thrown.getMessage()).isEqualTo("Пользователь с id 100 не найден");
    }

    @Test
    void whenRegistrationUser_thenReturnNewUser() throws Exception {
        int count = userRepository.getAll().size();
        User newUser = userService.registration(userEmail, userName, userPassword);
        assertThat(newUser.getId()).isEqualTo(newId);
        assertThat(newUser.getName()).isEqualTo(userName);
        assertThat(newUser.getPassword()).isEqualTo(userPassword);
        assertThat(newUser.getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(userRepository.getAll()).hasSize(count + 1);
    }

    @Test
    void whenRegistrationUser_withWrongEmail_thenReturnNull() {
        UserException thrown = assertThrows(UserException.class,
                () -> userService.registration(adminEmail, userName, userPassword));
        assertThat(thrown.getMessage()).isEqualTo("Пользователь с email admin@admin уже существует");
    }

    @Test
    void whenLogin_withCorrectData_thenReturnUser() throws Exception {
        User authenticatedUser = userService.login(adminEmail, adminPassword);
        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getId()).isEqualTo(adminId);
    }

    @Test
    void whenLogin_withCorrectEmailUpperCase_thenReturnUser() throws Exception {
        User authenticatedUser = userService.login(adminEmail.toUpperCase(), adminPassword);
        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getId()).isEqualTo(adminId);
    }

    @Test
    void whenLogin_withTruePasswordUpperCase_thenReturnNull() {
        UserException thrown = assertThrows(UserException.class, () ->
                userService.login(adminEmail, adminPassword.toUpperCase())
        );
        assertThat(thrown.getMessage()).isEqualTo("Email и пароль не верны");
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        userTest = userService.getById(idForUpdate);
        int count = userRepository.getAll().size();
        assertThat(userTest.getId()).isEqualTo(idForUpdate);
        assertThat(userTest.getRole()).isEqualTo(UserRole.ROLE_USER);

        userTest.setName(newUsername + idForUpdate);
        UserRequestDto userRequestDto = new UserRequestDto(userTest.getEmail(), userTest.getName());
        UserResponseDto updatedUser = userService.update(idForUpdate, userRequestDto);

        assertThat(updatedUser.getId()).isEqualTo(idForUpdate);
        assertThat(updatedUser.getName()).isEqualTo(newUsername + idForUpdate);
        assertThat(updatedUser.getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenUpdateUser_withWrongId_thenReturnNull() throws Exception {
        userTest = userService.getById(idForUpdate);
        UserRequestDto userRequestDto = new UserRequestDto(userTest.getEmail(), userTest.getName());
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> userService.update(wrongId, userRequestDto));
        assertThat(thrown.getMessage()).isEqualTo("Пользователь с id 100 не найден");
    }

    @Test
    void whenDeleteUserById_thenReturnTrue() throws Exception {
        int count = userRepository.getAll().size();
        boolean result = userService.delete(idForDelete);
        assertThat(result).isTrue();
        assertThat(userRepository.getAll()).hasSize(count - 1);
    }

    @Test
    void whenDeleteUser_withWrongId_thenReturnFalse() throws Exception {
        int count = userRepository.getAll().size();
        boolean result = userService.delete(wrongId);
        assertThat(result).isFalse();
        assertThat(userRepository.getAll()).hasSize(count);
    }
}