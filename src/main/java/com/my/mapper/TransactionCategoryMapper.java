package com.my.mapper;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.model.TransactionCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Интерфейс TransactionCategoryMapper для преобразования объектов типа TransactionCategory.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionCategoryMapper {

    TransactionCategoryMapper INSTANCE = Mappers.getMapper(TransactionCategoryMapper.class);

    void updateTransaction(TransactionCategoryRequestDto sourceUser, @MappingTarget TransactionCategory targetUser);

    TransactionCategory toEntity(TransactionCategoryRequestDto request);

    TransactionCategory toEntity(Long id, TransactionCategoryRequestDto request);

    TransactionCategoryResponseDto toDto(TransactionCategory entity);
}
