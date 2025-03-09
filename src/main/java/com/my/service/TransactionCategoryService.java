package com.my.service;

import com.my.model.TransactionCategory;

import java.util.List;

public interface TransactionCategoryService {
    List<TransactionCategory> getAll();

    TransactionCategory getById(Long id);

    TransactionCategory save(TransactionCategory transactionCategory);

    TransactionCategory update(Long id, TransactionCategory sourceTransactionCategory);

    boolean deleteById(Long id);
}
