package com.my.repository.impl;

import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    void testGetById_ExistingId() {
        User user = userRepository.getAll().get(0);
        Optional<User> foundUser = userRepository.getById(user.getId());
        assertAll(
                () -> assertThat(foundUser).isPresent(),
                () -> assertThat(user.getId()).isEqualTo(foundUser.get().getId())
        );
    }

    @Test
    void testGetById_NonExistingId() {
        Optional<User> foundUser = userRepository.getById(999L);
        assertThat(foundUser).isNotPresent();
    }

    @Test
    void testSave() {
        User newUser = new User("TestUser", "password", "Test", UserRole.ROLE_USER);
        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void testUpdate() {
        User user = userRepository.getAll().get(0);
        user.setName("UpdatedName");
        User updatedUser = userRepository.update(user);

        assertThat(updatedUser.getName()).isEqualTo("UpdatedName");
    }

    @Test
    void testDeleteById_ExistingId() {
        User user = userRepository.getAll().get(0);
        boolean isDeleted = userRepository.deleteById(user.getId());

        assertThat(isDeleted).isTrue();
        assertThat(userRepository.getById(user.getId())).isNotPresent();
    }

    @Test
    void testDeleteById_NonExistingId() {
        boolean isDeleted = userRepository.deleteById(999L);
        assertThat(isDeleted).isFalse();
    }

    @Test
    void testIsPresentByEmail_ExistingEmail() {
        boolean exists = userRepository.isPresentByEmail("Admin");
        assertThat(exists).isTrue();
    }

    @Test
    void testIsPresentByEmail_NonExistingEmail() {
        boolean exists = userRepository.isPresentByEmail("no@no.com");
        assertThat(exists).isFalse();
    }

    @Test
    void testGetByEmailAndPassword_ValidCredentials() {
        Optional<User> user = userRepository.getByEmailAndPassword("Admin", "aDmIn");
        assertThat(user).isPresent();
    }

    @Test
    void testGetByEmailAndPassword_InvalidCredentials() {
        Optional<User> user = userRepository.getByEmailAndPassword("Admin", "wrongPassword");
        assertThat(user).isNotPresent();
    }

    @Test
    void testGetByEmailAndPassword_BlockedUser() {
        User user = userRepository.getAll().get(0);
        user.setBlocked(true);
        userRepository.update(user);
        Optional<User> foundUser = userRepository.getByEmailAndPassword("Admin", "aDmIn");
        assertThat(foundUser).isNotPresent();
    }

}
