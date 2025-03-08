package com.my.service;

import com.my.model.User;

import java.util.List;

public interface UserService {
    User registration(String email, String name, String password);

    User login(String email, String password);

    User getById(Long id);

    User update(Long id, User sourceUser);

    boolean delete(Long id);

    List<User> getAll();
}
