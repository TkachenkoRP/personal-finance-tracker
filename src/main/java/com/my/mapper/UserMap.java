package com.my.mapper;

import com.my.dto.UserResponseDto;
import com.my.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMap {
    private final UserService userService;

    @Autowired
    public UserMap(UserService userService) {
        this.userService = userService;
    }

    public UserResponseDto fromId(Long id) {
        return id != null ? userService.getById(id) : null;
    }
}
