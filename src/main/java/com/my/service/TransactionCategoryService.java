package com.my.service;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
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
    List<TransactionCategoryResponseDto> getAll() throws SQLException;

    /**
     * Получение категории транзакции по её идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return категория транзакции с указанным идентификатором
     */
    TransactionCategoryResponseDto getById(Long id) throws SQLException;

    TransactionCategory getEntityById(Long id) throws SQLException;

    /**
     * Сохранение новой категории транзакции.
     *
     * @param request объект категории транзакции для сохранения
     * @return сохраненная категория транзакции
     */
    TransactionCategoryResponseDto save(TransactionCategoryRequestDto request) throws SQLException;

    /**
     * Обновление существующей категории транзакции.
     *
     * @param id      идентификатор категории транзакции
     * @param request объект категории транзакции с обновленной информацией
     * @return обновленная категория транзакции
     */
    TransactionCategoryResponseDto update(Long id, TransactionCategoryRequestDto request) throws SQLException;

    /**
     * Удаление категории транзакции по её идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return true, если категория транзакции была успешно удалена, иначе false
     */
    boolean deleteById(Long id) throws SQLException;
}
