package com.my.controller;

import com.my.controller.doc.TransactionCategoryControllerDoc;
import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.service.TransactionCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class TransactionCategoryController implements TransactionCategoryControllerDoc {
    private final TransactionCategoryService transactionCategoryService;

    @GetMapping
    public List<TransactionCategoryResponseDto> getAll() {
        return transactionCategoryService.getAll();
    }

    @GetMapping("/{id}")
    public TransactionCategoryResponseDto getById(@PathVariable("id") Long id) {
        return transactionCategoryService.getById(id);
    }

    @PostMapping
    public TransactionCategoryResponseDto post(@RequestBody @Valid TransactionCategoryRequestDto request) {
        return transactionCategoryService.save(request);
    }

    @PatchMapping("/{id}")
    public TransactionCategoryResponseDto patch(@PathVariable("id") Long id,
                                                @RequestBody @Valid TransactionCategoryRequestDto request) {
        return transactionCategoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        transactionCategoryService.deleteById(id);
    }
}
