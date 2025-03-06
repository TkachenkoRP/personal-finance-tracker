package com.my.service;

import com.my.model.User;

public interface UserService {
    User registration(String email, String password);

    User login(String email, String password);
}
