package com.my.mapper;

import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Интерфейс TransactionMapper для преобразования объектов типа Transaction.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TransactionCategoryMap.class, UserMap.class})
public interface TransactionMapper {
    /**
     * Экземпляр TransactionMapper для использования в приложении.
     */
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "date", source = "date", dateFormat = "d.M.yyyy")
    void updateTransaction(TransactionRequestDto sourceUser, @MappingTarget Transaction targetUser);

    @Mapping(target = "date", source = "date", dateFormat = "d.M.yyyy")
    Transaction toEntity(TransactionRequestDto request);

    @Mapping(target = "date", source = "date", dateFormat = "d.M.yyyy")
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "user", source = "userId")
    TransactionResponseDto toDto(Transaction entity);

    List<TransactionResponseDto> toDto(List<Transaction> entities);
}
