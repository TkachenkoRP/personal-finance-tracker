package com.my.mapper;

import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import com.my.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TransactionCategoryMap.class})
public interface GoalMapper {
    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    void updateGoal(GoalRequestDto sourceGoal, @MappingTarget Goal targetGoal);

    Goal toEntity(GoalRequestDto request);

    @Mapping(target = "category", source = "categoryId")
    GoalResponseDto toDto(Goal entity);

    List<GoalResponseDto> toDto(List<Goal> entities);
}
