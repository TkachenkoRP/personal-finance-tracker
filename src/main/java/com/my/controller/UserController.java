package com.my.controller;

import com.my.controller.doc.UserControllerDoc;
import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserControllerDoc {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserResponseDto patch(@PathVariable("id") Long id, @RequestBody @Valid UserRequestDto request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @PutMapping("/{id}/block")
    public void blockUser(@PathVariable("id") Long id) {
        userService.blockUser(id);
    }

    @PutMapping("/{id}/unblock")
    public void unblockUser(@PathVariable("id") Long id) {
        userService.unbBlockUser(id);
    }
}
