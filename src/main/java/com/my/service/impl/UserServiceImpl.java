package com.my.service.impl;

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
    public User getById(Long id) throws SQLException {
        return userRepository.getById(id).orElse(null);
    }

    @Override
    public User update(Long id, User sourceUser) throws SQLException {
        User updatedUser = getById(id);
        if (updatedUser == null || (!updatedUser.getEmail().equals(sourceUser.getEmail()) && !isEmailAvailable(sourceUser.getEmail()))) {
            return null;
        }
        UserMapper.INSTANCE.updateUser(sourceUser, updatedUser);
        return userRepository.update(updatedUser);
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        return userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() throws SQLException {
        return userRepository.getAll();
    }

    private User getUserByLoginAndPassword(String email, String password) throws SQLException {
        return userRepository.getByEmailAndPassword(email, password).orElse(null);
    }

    private boolean isEmailAvailable(String email) throws SQLException {
        return !userRepository.isPresentByEmail(email);
    }

    public boolean blockUser(Long userId) throws SQLException {
        User user = getById(userId);
        if (user != null) {
            user.setBlocked(true);
            update(userId, user);
            return true;
        }
        return false;
    }
}