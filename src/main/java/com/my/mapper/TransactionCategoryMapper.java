package com.my.mapper;

import com.my.model.TransactionCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionCategoryMapper {
    TransactionCategoryMapper INSTANCE = Mappers.getMapper(TransactionCategoryMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateTransaction(TransactionCategory sourceUser, @MappingTarget TransactionCategory targetUser);

    TransactionCategory copyTransactionCategory(TransactionCategory sourceUser);
}
