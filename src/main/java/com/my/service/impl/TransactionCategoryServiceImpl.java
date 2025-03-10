package com.my.service.impl;

import com.my.mapper.TransactionCategoryMapper;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;
import com.my.service.TransactionCategoryService;

import java.util.List;

public class TransactionCategoryServiceImpl implements TransactionCategoryService {

    private final TransactionCategoryRepository transactionCategoryRepository;

    public TransactionCategoryServiceImpl(TransactionCategoryRepository transactionCategoryRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    @Override
    public List<TransactionCategory> getAll() {
        return transactionCategoryRepository.getAll();
    }

    @Override
    public TransactionCategory getById(Long id) {
        return transactionCategoryRepository.getById(id).orElse(null);
    }

    @Override
    public TransactionCategory save(TransactionCategory transactionCategory) {
        if (transactionCategoryRepository.existsByCategoryNameIgnoreCase(transactionCategory.getCategoryName())) {
            return null;
        }
        return transactionCategoryRepository.save(transactionCategory);
    }

    @Override
    public TransactionCategory update(Long id, TransactionCategory sourceTransactionCategory) {
        TransactionCategory updatedTransactionCategory = getById(id);
        if (updatedTransactionCategory == null) {
            return null;
        }

        TransactionCategoryMapper.INSTANCE.updateTransaction(sourceTransactionCategory, updatedTransactionCategory);

        return transactionCategoryRepository.update(updatedTransactionCategory);
    }

    @Override
    public boolean deleteById(Long id) {
        return transactionCategoryRepository.deleteById(id);
    }
}