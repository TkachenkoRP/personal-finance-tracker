package com.my.service.impl;

import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("test@example.com", "password", "Test User", UserRole.ROLE_USER);
    }

    @Test
    void registration_ShouldReturnUser_WhenEmailIsAvailable() {
        when(userRepository.isPresentByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registration(user.getEmail(), user.getName(), user.getPassword());

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void registration_ShouldReturnNull_WhenEmailIsNotAvailable() {
        when(userRepository.isPresentByEmail(user.getEmail())).thenReturn(true);

        User registeredUser = userService.registration(user.getEmail(), user.getName(), user.getPassword());

        assertThat(registeredUser).isNull();
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreCorrect() {
        when(userRepository.getByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));

        User loggedInUser = userService.login(user.getEmail(), user.getPassword());

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void login_ShouldReturnNull_WhenCredentialsAreIncorrect() {
        when(userRepository.getByEmailAndPassword(user.getEmail(), "wrongPassword")).thenReturn(Optional.empty());

        User loggedInUser = userService.login(user.getEmail(), "wrongPassword");

        assertThat(loggedInUser).isNull();
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.getById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void getById_ShouldReturnNull_WhenUserDoesNotExist() {
        when(userRepository.getById(1L)).thenReturn(Optional.empty());

        User foundUser = userService.getById(1L);

        assertThat(foundUser).isNull();
    }

    @Test
    void update_ShouldReturnUpdatedUser_WhenEmailIsAvailable() {
        User sourceUser = new User("new@example.com", "newPassword", "New User", UserRole.ROLE_USER);
        when(userRepository.getById(1L)).thenReturn(Optional.of(user));
        when(userRepository.isPresentByEmail(sourceUser.getEmail())).thenReturn(false);
        when(userRepository.update(any(User.class))).thenReturn(user);

        User updatedUser = userService.update(1L, sourceUser);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(sourceUser.getEmail());
    }

    @Test
    void update_ShouldReturnNull_WhenEmailIsNotAvailable() {
        User sourceUser = new User("test@example.com", "newPassword", "New User", UserRole.ROLE_USER);
        when(userRepository.getById(1L)).thenReturn(Optional.of(user));
        when(userRepository.isPresentByEmail(sourceUser.getEmail())).thenReturn(true);

        User updatedUser = userService.update(1L, sourceUser);

        assertThat(updatedUser).isNull();
    }

    @Test
    void delete_ShouldReturnTrue_WhenUserDeletedSuccessfully() {
        when(userRepository.deleteById(1L)).thenReturn(true);

        boolean result = userService.delete(1L);

        assertThat(result).isTrue();
    }

    @Test
    void delete_ShouldReturnFalse_WhenUserNotDeleted() {
        when(userRepository.deleteById(1L)).thenReturn(false);

        boolean result = userService.delete(1L);

        assertThat(result).isFalse();
    }

    @Test
    void getAll_ShouldReturnListOfUsers() {
        when(userRepository.getAll()).thenReturn(List.of(user));

        List<User> users = userService.getAll();

        assertThat(users).isNotNull().hasSize(1);
    }

    @Test
    void blockUser_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.getById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.blockUser(1L);

        assertThat(result).isTrue();
        assertThat(user.isBlocked()).isTrue();
    }

    @Test
    void blockUser_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(userRepository.getById(1L)).thenReturn(Optional.empty());

        boolean result = userService.blockUser(1L);

        assertThat(result).isFalse();
    }
}
