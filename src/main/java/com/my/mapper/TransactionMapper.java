package com.my.mapper;

import com.my.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "type", ignore = true)
    void updateTransaction(Transaction sourceUser, @MappingTarget Transaction targetUser);

    Transaction copyTransaction(Transaction sourceUser);
}
