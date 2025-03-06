package com.my.service.impl;

import com.my.model.User;
import com.my.repository.UserRepository;
import com.my.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registration(String email, String password) {
        Optional<User> user = userRepository.getByEmail(email);
        if (user.isPresent()) {
            return null;
        }
        return userRepository.save(new User(email, password));
    }

    @Override
    public User login(String email, String password) {
        Optional<User> user = userRepository.getByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        return null;
    }
}
