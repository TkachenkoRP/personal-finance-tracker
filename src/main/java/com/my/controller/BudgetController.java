package com.my.controller;

import com.my.annotation.Audition;
import com.my.controller.doc.BudgetControllerDoc;
import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.service.BudgetService;
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
@RequestMapping("/api/budget")
@RequiredArgsConstructor
@Audition
public class BudgetController implements BudgetControllerDoc {
    private final BudgetService budgetService;

    @GetMapping
    public List<BudgetResponseDto> getAll() {
        return budgetService.geAll();
    }

    @GetMapping("/{id}")
    public BudgetResponseDto getById(@PathVariable("id") Long id) {
        return budgetService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<BudgetResponseDto> getByUserId(@PathVariable("userId") Long userId) {
        return budgetService.getAllBudgetsByUserId(userId);
    }

    @PostMapping
    public BudgetResponseDto post(@RequestBody @Validated(BudgetRequestDto.Post.class) BudgetRequestDto request) {
        return budgetService.save(UserManager.getLoggedInUser().getId(), request);
    }

    @PatchMapping("/{id}")
    public BudgetResponseDto patch(@PathVariable("id") Long id, @RequestBody @Validated(BudgetRequestDto.Update.class) BudgetRequestDto request) {
        return budgetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        budgetService.deleteById(id);
    }
}
