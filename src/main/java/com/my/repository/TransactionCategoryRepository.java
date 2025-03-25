package com.my.repository;

import com.my.model.TransactionCategory;

import java.sql.SQLException;
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
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    List<TransactionCategory> getAll() throws SQLException;

    /**
     * Получение категории транзакции по ее идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return объект Optional, содержащий найденную категорию, или пустой, если категория не найдена
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    Optional<TransactionCategory> getById(Long id) throws SQLException;

    /**
     * Сохранение новой категории транзакции.
     *
     * @param entity объект категории транзакции для сохранения
     * @return сохраненный объект категории транзакции
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    TransactionCategory save(TransactionCategory entity) throws SQLException;

    /**
     * Обновление существующей категории транзакции.
     *
     * @param entity объект категории транзакции с обновленными данными
     * @return обновленный объект категории транзакции
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    TransactionCategory update(TransactionCategory entity) throws SQLException;

    /**
     * Удаление категории транзакции по ее идентификатору.
     *
     * @param id идентификатор категории транзакции для удаления
     * @return true, если категория была успешно удалена, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean deleteById(Long id) throws SQLException;

    /**
     * Проверка существования категории по ее имени (без учета регистра).
     *
     * @param categoryName название категории для проверки
     * @return true, если категория с указанным именем существует, иначе false
     * @throws SQLException если происходит ошибка при доступе к базе данных
     */
    boolean existsByCategoryNameIgnoreCase(String categoryName) throws SQLException;
}
