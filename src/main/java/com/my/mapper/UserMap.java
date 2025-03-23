package com.my.mapper;

import com.my.dto.UserResponseDto;
import com.my.service.UserService;
import com.my.service.impl.UserServiceImpl;

import java.sql.SQLException;

public class UserMap {
    private final UserService userService;

    public UserMap() {
        this.userService = new UserServiceImpl();
    }

    public UserResponseDto fromId(Long id) {
        UserResponseDto user;
        try {
            user = id != null ? userService.getById(id) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
