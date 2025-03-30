package com.my.service;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;

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
    List<TransactionCategoryResponseDto> getAll();

    /**
     * Получение категории транзакции по её идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return категория транзакции с указанным идентификатором
     */
    TransactionCategoryResponseDto getById(Long id);

    /**
     * Сохранение новой категории транзакции.
     *
     * @param request объект категории транзакции для сохранения
     * @return сохраненная категория транзакции
     */
    TransactionCategoryResponseDto save(TransactionCategoryRequestDto request);

    /**
     * Обновление существующей категории транзакции.
     *
     * @param id      идентификатор категории транзакции
     * @param request объект категории транзакции с обновленной информацией
     * @return обновленная категория транзакции
     */
    TransactionCategoryResponseDto update(Long id, TransactionCategoryRequestDto request);

    /**
     * Удаление категории транзакции по её идентификатору.
     *
     * @param id идентификатор категории транзакции
     * @return true, если категория транзакции была успешно удалена, иначе false
     */
    boolean deleteById(Long id);
}
