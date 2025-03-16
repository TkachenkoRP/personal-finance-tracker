package com.my.service.impl;

import com.my.model.User;
import com.my.model.UserRole;
import com.my.service.AbstractTestContainer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceImplContainerTest extends AbstractTestContainer {
    final Long adminId = 1L;
    final String adminEmail = "admin@admin";
    final String adminPassword = "aDmIn";
    final String userEmail = "user@user";
    final String userName = "user";
    final String userPassword = "user";
    final Long idForUpdate = 3L;
    final String newUsername = "NewUserName";
    User userTest;
    final Long wrongId = 100L;
    final Long newId = 5L;
    final Long idForDelete = 4L;

    @Test
    void whenGetAllUsers_thenReturnAllUsers() throws Exception {
        int count = userRepository.getAll().size();
        List<User> userList = userService.getAll();
        assertThat(userList).hasSize(count);
    }

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        User foundUser = userService.getById(adminId);
        assertThat(foundUser.getId()).isEqualTo(adminId);
        assertThat(foundUser.getEmail()).isEqualTo(adminEmail);
    }

    @Test
    void whenGetUserById_withWrongId_thenReturnNull() throws Exception {
        User foundUser = userService.getById(wrongId);
        assertThat(foundUser).isNull();
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
    void whenRegistrationUser_withWrongEmail_thenReturnNull() throws Exception {
        int count = userRepository.getAll().size();
        User newUser = userService.registration(adminEmail, userName, userPassword);
        assertThat(newUser).isNull();
        assertThat(userRepository.getAll()).hasSize(count);
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
    void whenLogin_withWrongPassword_thenReturnNull() throws Exception {
        User authenticatedUser = userService.login(adminEmail, adminPassword + 1);
        assertThat(authenticatedUser).isNull();
    }

    @Test
    void whenLogin_withTruePasswordUpperCase_thenReturnNull() throws Exception {
        User authenticatedUser = userService.login(adminEmail, adminPassword.toUpperCase());
        assertThat(authenticatedUser).isNull();
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        userTest = userService.getById(idForUpdate);
        int count = userRepository.getAll().size();
        assertThat(userTest.getId()).isEqualTo(idForUpdate);
        assertThat(userTest.getRole()).isEqualTo(UserRole.ROLE_USER);

        userTest.setName(newUsername+idForUpdate);
        User updatedUser = userService.update(idForUpdate, userTest);

        assertThat(updatedUser.getId()).isEqualTo(idForUpdate);
        assertThat(updatedUser.getName()).isEqualTo(newUsername+idForUpdate);
        assertThat(updatedUser.getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(userRepository.getAll()).hasSize(count);
    }

    @Test
    void whenUpdateUser_withWrongId_thenReturnNull() throws Exception {
        userTest = userService.getById(idForUpdate);
        User updatedUser = userService.update(wrongId, userTest);
        assertThat(updatedUser).isNull();
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