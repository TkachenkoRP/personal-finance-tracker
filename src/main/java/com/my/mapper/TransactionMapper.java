package com.my.mapper;

import com.my.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Интерфейс TransactionMapper для преобразования объектов типа Transaction.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper
public interface TransactionMapper {
    /**
     * Экземпляр TransactionMapper для использования в приложении.
     */
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    /**
     * Обновляет существующий объект Transaction, копируя значения из другого объекта Transaction.
     * Поля id, user, date и type будут проигнорированы при обновлении.
     *
     * @param sourceUser объект Transaction, из которого будут скопированы значения
     * @param targetUser объект Transaction, который будет обновлен
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "type", ignore = true)
    void updateTransaction(Transaction sourceUser, @MappingTarget Transaction targetUser);
}
