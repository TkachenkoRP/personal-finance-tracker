package com.my.repository;

import com.my.model.TransactionCategory;

/**
 * Интерфейс репозитория для управления категориями транзакций.
 * Наследует базовый интерфейс {@link BaseRepository} для выполнения основных операций
 * с категориями транзакций.
 */
public interface TransactionCategoryRepository extends BaseRepository<TransactionCategory> {
    /**
     * Проверка существования категории транзакции по её имени, игнорируя регистр.
     *
     * @param categoryName имя категории для проверки
     * @return true, если категория с указанным именем существует, иначе false
     */
    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
