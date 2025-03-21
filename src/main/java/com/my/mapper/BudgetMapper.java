package com.my.mapper;

import com.my.model.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BudgetMapper {
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateTransaction(Budget sourceGoal, @MappingTarget Budget targetGoal);
}
