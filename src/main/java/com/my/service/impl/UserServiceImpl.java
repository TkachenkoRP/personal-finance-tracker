package com.my.service.impl;

import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.mapper.UserMapper;
import com.my.model.User;
import com.my.model.UserRole;
import com.my.repository.UserRepository;
import com.my.repository.impl.JdbcUserRepository;
import com.my.service.UserService;

import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl() {
        this(new JdbcUserRepository());
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registration(String email, String name, String password) throws SQLException {
        if (!isEmailAvailable(email)) {
            return null;
        }
        return userRepository.save(new User(email, password, name, UserRole.ROLE_USER));
    }

    @Override
    public User login(String email, String password) throws SQLException {
        return getUserByLoginAndPassword(email, password);
    }

    @Override
    public UserResponseDto getById(Long id) throws SQLException {
        User user = userRepository.getById(id).orElse(null);
        UserResponseDto userResponseDto = UserMapper.INSTANCE.toDto(user);
        return userResponseDto;
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto sourceUser) throws SQLException {
        User updatedUser = userRepository.getById(id).orElse(null);
        if (updatedUser == null || (!updatedUser.getEmail().equals(sourceUser.getEmail()) && !isEmailAvailable(sourceUser.getEmail()))) {
            return null;
        }
        UserMapper.INSTANCE.updateUser(sourceUser, updatedUser);
        User updated = userRepository.update(updatedUser);
        UserResponseDto userResponseDto = UserMapper.INSTANCE.toDto(updated);
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
        return userRepository.getByEmailAndPassword(email, password).orElse(null);
    }

    private boolean isEmailAvailable(String email) throws SQLException {
        return !userRepository.isPresentByEmail(email);
    }

    public boolean blockUser(Long userId) throws SQLException {
        User user = userRepository.getById(userId).orElse(null);
        if (user != null) {
            user.setBlocked(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}