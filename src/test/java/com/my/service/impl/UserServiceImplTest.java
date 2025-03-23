package com.my.service.impl;

import com.my.dto.UserResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.UserException;
import com.my.mapper.UserMapper;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void registration_ShouldReturnUser_WhenEmailIsAvailable() throws Exception {
        when(userRepository.isEmailAvailable(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserException thrown = assertThrows(UserException.class, () -> {
            userService.registration(user.getEmail(), user.getName(), user.getPassword());
        });
        assertThat(thrown.getMessage()).isEqualTo("Пользователь с email test@example.com уже существует");
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreCorrect() throws Exception {
        when(userRepository.getByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));

        User loggedInUser = userService.login(user.getEmail(), user.getPassword());

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void login_ShouldThrowUserException_WhenCredentialsAreIncorrect() throws Exception {
        when(userRepository.getByEmailAndPassword(user.getEmail(), "wrongPassword")).thenReturn(Optional.empty());

        UserException thrown = assertThrows(UserException.class, () -> userService.login(user.getEmail(), "wrongPassword"));

        assertThat(thrown.getMessage()).isEqualTo("Email и пароль не верны");
    }

    @Test
    void getById_ShouldReturnUserResponseDto_WhenUserExists() throws Exception {
        when(userRepository.getById(1L)).thenReturn(Optional.of(user));
        UserResponseDto userResponseDto = UserMapper.INSTANCE.toDto(user);

        UserResponseDto foundUser = userService.getById(1L);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() throws Exception {
        when(userRepository.getById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            userService.getById(1L);
        });

        assertThat(thrown.getMessage()).contains("Пользователь с id 1 не найден");
    }

    @Test
    void delete_ShouldReturnTrue_WhenUserDeletedSuccessfully() throws Exception {
        when(userRepository.deleteById(1L)).thenReturn(true);

        boolean result = userService.delete(1L);

        assertThat(result).isTrue();
    }

    @Test
    void delete_ShouldReturnFalse_WhenUserNotDeleted() throws Exception {
        when(userRepository.deleteById(1L)).thenReturn(false);

        boolean result = userService.delete(1L);

        assertThat(result).isFalse();
    }

    @Test
    void getAll_ShouldReturnListOfUserResponseDtos() throws Exception {
        when(userRepository.getAll()).thenReturn(List.of(user));
        List<UserResponseDto> users = userService.getAll();
        assertThat(users).isNotNull().hasSize(1);
    }

    @Test
    void blockUser_ShouldReturnFalse_WhenUserDoesNotExist() throws Exception {
        when(userRepository.getById(1L)).thenReturn(Optional.empty());

        boolean result = userService.blockUser(1L);

        assertThat(result).isFalse();
    }
}
