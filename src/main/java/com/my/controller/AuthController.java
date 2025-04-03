package com.my.controller;

import com.my.controller.doc.AuthControllerDoc;
import com.my.dto.UserLoginRequestDto;
import com.my.dto.UserRegisterRequestDto;
import com.my.dto.UserResponseDto;
import com.my.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDoc {
    private final UserService userService;

    @PostMapping("/login")
    public UserResponseDto login(@Valid @RequestBody UserLoginRequestDto request) {
        return userService.login(request.getEmail(), request.getPassword());
    }

    @GetMapping("/logout")
    public void logout() {
        userService.logout();
    }

    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody UserRegisterRequestDto request) {
        return userService.registration(request.getEmail(), request.getName(), request.getPassword());
    }
}
