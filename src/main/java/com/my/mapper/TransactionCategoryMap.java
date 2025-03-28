package com.my.mapper;

import com.my.dto.TransactionCategoryResponseDto;
import com.my.service.TransactionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategoryMap {
    private final TransactionCategoryService transactionCategoryService;

    @Autowired
    public TransactionCategoryMap(TransactionCategoryService transactionCategoryService) {
        this.transactionCategoryService = transactionCategoryService;
    }

    public TransactionCategoryResponseDto fromId(Long id) {
        return id != null ? transactionCategoryService.getById(id) : null;
    }
}
