package com.my.repository;

import com.my.model.TransactionCategory;

public interface TransactionCategoryRepository extends BaseRepository<TransactionCategory> {
    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
