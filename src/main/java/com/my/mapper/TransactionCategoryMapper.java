package com.my.mapper;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.model.TransactionCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Интерфейс TransactionCategoryMapper для преобразования объектов типа TransactionCategory.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionCategoryMapper {

    /**
     * Экземпляр TransactionCategoryMapper для использования в приложении.
     */
    TransactionCategoryMapper INSTANCE = Mappers.getMapper(TransactionCategoryMapper.class);

    /**
     * Обновляет существующий объект TransactionCategory на основе данных из TransactionCategoryRequestDto.
     *
     * @param sourceUser объект TransactionCategoryRequestDto, содержащий данные для обновления
     * @param targetUser объект TransactionCategory, который будет обновлен
     */
    void updateTransaction(TransactionCategoryRequestDto sourceUser, @MappingTarget TransactionCategory targetUser);

    /**
     * Преобразует объект TransactionCategoryRequestDto в сущность TransactionCategory.
     *
     * @param request объект TransactionCategoryRequestDto, содержащий данные для создания категории транзакции
     * @return объект TransactionCategory, созданный на основе данных из request
     */
    TransactionCategory toEntity(TransactionCategoryRequestDto request);

    /**
     * Преобразует сущность TransactionCategory в объект TransactionCategoryResponseDto.
     *
     * @param entity объект TransactionCategory, который будет преобразован в DTO
     * @return объект TransactionCategoryResponseDto, созданный на основе данных из entity
     */
    TransactionCategoryResponseDto toDto(TransactionCategory entity);

    /**
     * Преобразует список сущностей TransactionCategory в список объектов TransactionCategoryResponseDto.
     *
     * @param entities список объектов TransactionCategory, которые будут преобразованы в DTO
     * @return список объектов TransactionCategoryResponseDto, созданный на основе данных из entities
     */
    List<TransactionCategoryResponseDto> toDto(List<TransactionCategory> entities);
}
