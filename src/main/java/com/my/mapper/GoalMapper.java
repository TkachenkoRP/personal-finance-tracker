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

/**
 * Интерфейс GoalMapper для преобразования объектов типа Goal.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TransactionCategoryMap.class})
public interface GoalMapper {
    /**
     * Экземпляр GoalMapper для использования в приложении.
     */
    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    /**
     * Обновляет существующий объект Goal на основе данных из GoalRequestDto.
     *
     * @param sourceGoal объект GoalRequestDto, содержащий данные для обновления
     * @param targetGoal объект Goal, который будет обновлен
     */
    void updateGoal(GoalRequestDto sourceGoal, @MappingTarget Goal targetGoal);

    /**
     * Преобразует объект GoalRequestDto в сущность Goal.
     *
     * @param request объект GoalRequestDto, содержащий данные для создания цели
     * @return объект Goal, созданный на основе данных из request
     */
    Goal toEntity(GoalRequestDto request);

    /**
     * Преобразует сущность Goal в объект GoalResponseDto.
     *
     * @param entity объект Goal, который будет преобразован в DTO
     * @return объект GoalResponseDto, созданный на основе данных из entity
     */
    @Mapping(target = "category", source = "categoryId")
    GoalResponseDto toDto(Goal entity);

    /**
     * Преобразует список сущностей Goal в список объектов GoalResponseDto.
     *
     * @param entities список объектов Goal, которые будут преобразованы в DTO
     * @return список объектов GoalResponseDto, созданный на основе данных из entities
     */
    List<GoalResponseDto> toDto(List<Goal> entities);
}
