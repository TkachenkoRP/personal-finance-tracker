package com.my.controller;

import com.my.annotation.Audition;
import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.service.GoalService;
import com.my.service.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
@Audition
public class GoalController {
    private final GoalService goalService;

    @GetMapping
    public List<GoalResponseDto> getAll() {
        return goalService.getAllGoalsByUserId(UserManager.getLoggedInUser().getId());
    }

    @GetMapping("/{id}")
    public GoalResponseDto getById(@PathVariable("id") Long id) {
        return goalService.getById(id);
    }

    @PostMapping
    public GoalResponseDto post(@RequestBody @Validated(GoalRequestDto.Post.class) GoalRequestDto request) {
        return goalService.save(UserManager.getLoggedInUser().getId(), request);
    }

    @PatchMapping("/{id}")
    public GoalResponseDto patch(@PathVariable("id") Long id, @RequestBody @Validated(GoalRequestDto.Update.class) GoalRequestDto request) {
        return goalService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        goalService.deleteById(id);
    }
}
