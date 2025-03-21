package com.my.mapper;

import com.my.model.TransactionCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Интерфейс TransactionCategoryMapper для преобразования объектов типа TransactionCategory.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper
public interface TransactionCategoryMapper {
    /**
     * Экземпляр TransactionCategoryMapper для использования в приложении.
     */
    TransactionCategoryMapper INSTANCE = Mappers.getMapper(TransactionCategoryMapper.class);

    /**
     * Обновляет существующий объект TransactionCategory, копируя значения из другого объекта TransactionCategory.
     * Поле id будет проигнорировано при обновлении.
     *
     * @param sourceUser объект TransactionCategory, из которого будут скопированы значения
     * @param targetUser объект TransactionCategory, который будет обновлен
     */
    @Mapping(target = "id", ignore = true)
    void updateTransaction(TransactionCategory sourceUser, @MappingTarget TransactionCategory targetUser);
}
