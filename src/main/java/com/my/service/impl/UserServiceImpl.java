package com.my.service.impl;

import com.my.mapper.UserMapper;
import com.my.model.User;
import com.my.repository.UserRepository;
import com.my.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registration(String email, String name, String password) {

        boolean isEmailAvailable = isEmailAvailable(email);

        if (isEmailAvailable) {
            return null;
        }

        return userRepository.save(new User(email, password, name));
    }

    @Override
    public User login(String email, String password) {
        return getUserByLoginAndPassword(email, password);
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id).orElse(null);
    }

    @Override
    public User update(Long id, User sourceUser) {
        if (!isEmailAvailable(sourceUser.getEmail())) {
            return null;
        }

        User updatedUser = getById(id);

        if (updatedUser == null) {
            return null;
        }

        UserMapper.INSTANCE.updateUser(sourceUser, updatedUser);

        return userRepository.update(updatedUser);
    }

    @Override
    public boolean delete(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    private User getUserByLoginAndPassword(String email, String password) {
        return userRepository.getByEmailAndPassword(email, password).orElse(null);
    }

    private boolean isEmailAvailable(String email) {
        return userRepository.isPresentByEmail(email);
    }
}
