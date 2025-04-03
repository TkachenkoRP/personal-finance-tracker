package com.my.service.impl;

import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.UserException;
import com.my.mapper.UserMapper;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.UserRepository;
import com.my.service.UserManager;
import com.my.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String USER_NOT_FOUND = "Пользователь с id {0} не найден";
    private static final String EMAIL_ALREADY_EXISTS = "Пользователь с email {0} уже существует";

    @Override
    public UserResponseDto registration(String email, String name, String password) {
        if (isEmailOccupied(email)) {
            throw new UserException(
                    MessageFormat.format(EMAIL_ALREADY_EXISTS, email)
            );
        }
        User user = userRepository.save(new User(email, password, name, UserRole.ROLE_USER));
        UserManager.setLoggedInUser(user);
        UserResponseDto userResponseDto = userMapper.toDto(user);
        log.debug("Registration user: {}", userResponseDto);
        return userResponseDto;
    }

    @Override
    public UserResponseDto login(String email, String password) {
        User user = getUserByLoginAndPassword(email, password);
        UserManager.setLoggedInUser(user);
        UserResponseDto userResponseDto = userMapper.toDto(user);
        log.debug("Login user: {}", userResponseDto);
        return userResponseDto;
    }

    @Override
    public void logout() {
        UserManager.setLoggedInUser(null);
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = getEntityUserById(id);
        UserResponseDto userResponseDto = userMapper.toDto(user);
        log.debug("Get user by id: {}", userResponseDto);
        return userResponseDto;
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto sourceUser) {
        User updatedUser = getEntityUserById(id);
        if (!updatedUser.getEmail().equals(sourceUser.getEmail()) && isEmailOccupied(sourceUser.getEmail())) {
            throw new UserException(
                    MessageFormat.format(EMAIL_ALREADY_EXISTS, sourceUser.getEmail())
            );
        }
        userMapper.updateUser(sourceUser, updatedUser);
        User updated = userRepository.update(updatedUser);
        UserResponseDto userResponseDto = userMapper.toDto(updated);
        log.debug("Update user: {}", userResponseDto);
        return userResponseDto;
    }

    @Override
    public boolean delete(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public List<UserResponseDto> getAll() {
        List<User> users = userRepository.getAll();
        List<UserResponseDto> responseDtoList = userMapper.toDto(users);
        log.debug("Get all users");
        return responseDtoList;
    }

    private User getUserByLoginAndPassword(String email, String password) {
        return userRepository.getByEmailAndPassword(email, password).orElseThrow(
                () -> new UserException("Email и пароль не верны")
        );
    }

    private User getEntityUserById(Long id) {
        return userRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(USER_NOT_FOUND, id))
        );
    }

    private boolean isEmailOccupied(String email) {
        return userRepository.isEmailOccupied(email);
    }

    @Override
    public boolean blockUser(Long userId) {
        return userRepository.blockUserById(userId);
    }

    @Override
    public boolean unbBlockUser(long userId) {
        return userRepository.unBlockUserById(userId);
    }
}