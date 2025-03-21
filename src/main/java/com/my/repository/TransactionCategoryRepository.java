package com.my.repository;

import com.my.model.TransactionCategory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TransactionCategoryRepository {
    List<TransactionCategory> getAll() throws SQLException;

    Optional<TransactionCategory> getById(Long id) throws SQLException;

    TransactionCategory save(TransactionCategory entity) throws SQLException;

    TransactionCategory update(TransactionCategory entity) throws SQLException;

    boolean deleteById(Long id) throws SQLException;

    boolean existsByCategoryNameIgnoreCase(String categoryName) throws SQLException;
}
