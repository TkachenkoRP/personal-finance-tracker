package com.my.service;

import com.my.model.TransactionCategory;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для управления категориями транзакций.
 */
public interface TransactionCategoryService {
    /**
     * Получение списка всех категорий транзакций.
     *
     * @return список всех категорий транзакций
     */
    List<TransactionCategory> getAll() throws SQLException;

    /**
     * Получение категории транзакции по её идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return категория транзакции с указанным идентификатором
     */
    TransactionCategory getById(Long id) throws SQLException;

    /**
     * Сохранение новой категории транзакции.
     *
     * @param transactionCategory объект категории транзакции для сохранения
     * @return сохраненная категория транзакции
     */
    TransactionCategory save(TransactionCategory transactionCategory) throws SQLException;

    /**
     * Обновление существующей категории транзакции.
     *
     * @param id                        идентификатор категории транзакции
     * @param sourceTransactionCategory объект категории транзакции с обновленной информацией
     * @return обновленная категория транзакции
     */
    TransactionCategory update(Long id, TransactionCategory sourceTransactionCategory) throws SQLException;

    /**
     * Удаление категории транзакции по её идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return true, если категория транзакции была успешно удалена, иначе false
     */
    boolean deleteById(Long id) throws SQLException;
}
