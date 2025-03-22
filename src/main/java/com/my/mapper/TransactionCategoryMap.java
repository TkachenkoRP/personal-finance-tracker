package com.my.mapper;

import com.my.dto.TransactionCategoryResponseDto;
import com.my.service.TransactionCategoryService;
import com.my.service.impl.TransactionCategoryServiceImpl;

import java.sql.SQLException;

public class TransactionCategoryMap {
    private final TransactionCategoryService transactionCategoryService;

    public TransactionCategoryMap() {
        this.transactionCategoryService = new TransactionCategoryServiceImpl();
    }

    public TransactionCategoryResponseDto fromId(Long id) {
        TransactionCategoryResponseDto transactionCategory;
        try {
            transactionCategory = id != null ? transactionCategoryService.getById(id) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactionCategory;
    }
}
