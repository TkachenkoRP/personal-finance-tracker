package com.my.service.impl;

import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.UserException;
import com.my.mapper.UserMapper;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.UserRepository;
import com.my.repository.impl.JdbcUserRepository;
import com.my.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getRootLogger();
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "Пользователь с id {0} не найден";
    private static final String EMAIL_ALREADY_EXISTS = "Пользователь с email {0} уже существует";

    public UserServiceImpl() {
        this(new JdbcUserRepository());
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registration(String email, String name, String password) throws SQLException {
        if (!isEmailAvailable(email)) {
            throw new UserException(
                    MessageFormat.format(EMAIL_ALREADY_EXISTS, email)
            );
        }
        return userRepository.save(new User(email, password, name, UserRole.ROLE_USER));
    }

    @Override
    public User login(String email, String password) throws SQLException {
        return getUserByLoginAndPassword(email, password);
    }

    @Override
    public UserResponseDto getById(Long id) throws SQLException {
        User user = userRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(USER_NOT_FOUND, id))
        );
        UserResponseDto userResponseDto = UserMapper.INSTANCE.toDto(user);
        logger.log(Level.DEBUG, "Get user by id: {}", userResponseDto);
        return userResponseDto;
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto sourceUser) throws SQLException {
        User updatedUser = userRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(USER_NOT_FOUND, id))
        );
        if (!updatedUser.getEmail().equals(sourceUser.getEmail()) && !isEmailAvailable(sourceUser.getEmail())) {
            throw new UserException(
                    MessageFormat.format(EMAIL_ALREADY_EXISTS, sourceUser.getEmail())
            );
        }
        UserMapper.INSTANCE.updateUser(sourceUser, updatedUser);
        User updated = userRepository.update(updatedUser);
        UserResponseDto userResponseDto = UserMapper.INSTANCE.toDto(updated);
        logger.log(Level.DEBUG, "Update user: {}", userResponseDto);
        return userResponseDto;
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return userRepository.deleteById(id);
    }

    @Override
    public List<UserResponseDto> getAll() throws SQLException {
        return userRepository.getAll().stream().map(UserMapper.INSTANCE::toDto).toList();
    }

    private User getUserByLoginAndPassword(String email, String password) throws SQLException {
        return userRepository.getByEmailAndPassword(email, password).orElseThrow(
                () -> new UserException("Email и пароль не верны")
        );
    }

    private boolean isEmailAvailable(String email) throws SQLException {
        return userRepository.isEmailAvailable(email);
    }

    public boolean blockUser(Long userId) throws SQLException {
        return userRepository.blockUserById(userId);
    }

    @Override
    public boolean unBlockUser(long userId) throws SQLException {
        return userRepository.unBlockUserById(userId);
    }
}