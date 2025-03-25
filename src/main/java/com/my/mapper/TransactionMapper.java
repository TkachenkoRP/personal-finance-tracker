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

    /**
     * Обновляет существующий объект Transaction на основе данных из TransactionRequestDto.
     *
     * @param sourceUser объект TransactionRequestDto, содержащий данные для обновления
     * @param targetUser объект Transaction, который будет обновлен
     */
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "date", source = "date", dateFormat = "d.M.yyyy")
    void updateTransaction(TransactionRequestDto sourceUser, @MappingTarget Transaction targetUser);

    /**
     * Преобразует объект TransactionRequestDto в сущность Transaction.
     *
     * @param request объект TransactionRequestDto, содержащий данные для создания транзакции
     * @return объект Transaction, созданный на основе данных из request
     */
    @Mapping(target = "date", source = "date", dateFormat = "d.M.yyyy")
    Transaction toEntity(TransactionRequestDto request);

    /**
     * Преобразует сущность Transaction в объект TransactionResponseDto.
     *
     * @param entity объект Transaction, который будет преобразован в DTO
     * @return объект TransactionResponseDto, созданный на основе данных из entity
     */
    @Mapping(target = "date", source = "date", dateFormat = "d.M.yyyy")
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "user", source = "userId")
    TransactionResponseDto toDto(Transaction entity);

    /**
     * Преобразует список сущностей Transaction в список объектов TransactionResponseDto.
     *
     * @param entities список объектов Transaction, которые будут преобразованы в DTO
     * @return список объектов TransactionResponseDto, созданный на основе данных из entities
     */
    List<TransactionResponseDto> toDto(List<Transaction> entities);
}
