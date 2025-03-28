package com.my.repository;

import com.my.model.TransactionCategory;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс TransactionCategoryRepository для работы с категориями транзакций.
 */
public interface TransactionCategoryRepository {
    /**
     * Получение всех категорий транзакций.
     *
     * @return список всех категорий транзакций
     */
    List<TransactionCategory> getAll();

    /**
     * Получение категории транзакции по ее идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return объект Optional, содержащий найденную категорию, или пустой, если категория не найдена
     */
    Optional<TransactionCategory> getById(Long id);

    /**
     * Сохранение новой категории транзакции.
     *
     * @param entity объект категории транзакции для сохранения
     * @return сохраненный объект категории транзакции
     */
    TransactionCategory save(TransactionCategory entity);

    /**
     * Обновление существующей категории транзакции.
     *
     * @param entity объект категории транзакции с обновленными данными
     * @return обновленный объект категории транзакции
     */
    TransactionCategory update(TransactionCategory entity);

    /**
     * Удаление категории транзакции по ее идентификатору.
     *
     * @param id идентификатор категории транзакции для удаления
     * @return true, если категория была успешно удалена, иначе false
     */
    boolean deleteById(Long id);

    /**
     * Проверка существования категории по ее имени (без учета регистра).
     *
     * @param categoryName название категории для проверки
     * @return true, если категория с указанным именем существует, иначе false
     */
    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
