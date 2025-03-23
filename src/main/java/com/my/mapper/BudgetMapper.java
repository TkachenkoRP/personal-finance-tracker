package com.my.mapper;

import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import com.my.model.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TransactionCategoryMap.class})
public interface BudgetMapper {
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    @Mapping(target = "periodStart", source = "periodStart", dateFormat = "d.M.yyyy")
    @Mapping(target = "periodEnd", source = "periodEnd", dateFormat = "d.M.yyyy")
    @Mapping(target = "targetGoal.categoryId", source = "categoryId")
    void updateBudget(BudgetRequestDto sourceGoal, @MappingTarget Budget targetGoal);

    @Mapping(target = "periodStart", source = "periodStart", dateFormat = "d.M.yyyy")
    @Mapping(target = "periodEnd", source = "periodEnd", dateFormat = "d.M.yyyy")
    Budget toEntity(BudgetRequestDto request);

    @Mapping(target = "periodStart", source = "request.periodStart", dateFormat = "d.M.yyyy")
    @Mapping(target = "periodEnd", source = "request.periodEnd", dateFormat = "d.M.yyyy")
    Budget toEntity(Long id, BudgetRequestDto request);

    List<BudgetResponseDto> toDto(List<Budget> entities);

    @Mapping(target = "periodStart", source = "periodStart", dateFormat = "d.M.yyyy")
    @Mapping(target = "periodEnd", source = "periodEnd", dateFormat = "d.M.yyyy")
    @Mapping(target = "category", source = "categoryId")
    BudgetResponseDto toDto(Budget entity);
}
