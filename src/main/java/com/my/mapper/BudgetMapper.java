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

/**
 * Интерфейс BudgetMapper для преобразования объектов типа Budget.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TransactionCategoryMap.class})
public interface BudgetMapper {
    /**
     * Экземпляр BudgetMapper для использования в приложении.
     */
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    /**
     * Обновляет существующий объект Budget на основе данных из BudgetRequestDto.
     *
     * @param sourceGoal объект BudgetRequestDto, содержащий данные для обновления
     * @param targetGoal объект Budget, который будет обновлен
     */
    @Mapping(target = "periodStart", source = "periodStart", dateFormat = "d.M.yyyy")
    @Mapping(target = "periodEnd", source = "periodEnd", dateFormat = "d.M.yyyy")
    @Mapping(target = "targetGoal.categoryId", source = "categoryId")
    void updateBudget(BudgetRequestDto sourceGoal, @MappingTarget Budget targetGoal);

    /**
     * Преобразует объект BudgetRequestDto в сущность Budget.
     *
     * @param request объект BudgetRequestDto, содержащий данные для создания бюджета
     * @return объект Budget, созданный на основе данных из request
     */
    @Mapping(target = "periodStart", source = "periodStart", dateFormat = "d.M.yyyy")
    @Mapping(target = "periodEnd", source = "periodEnd", dateFormat = "d.M.yyyy")
    Budget toEntity(BudgetRequestDto request);

    /**
     * Преобразует список сущностей Budget в список объектов BudgetResponseDto.
     *
     * @param entities список объектов Budget, которые будут преобразованы в DTO
     * @return список объектов BudgetResponseDto, созданный на основе данных из entities
     */
    List<BudgetResponseDto> toDto(List<Budget> entities);

    /**
     * Преобразует сущность Budget в объект BudgetResponseDto.
     *
     * @param entity объект Budget, который будет преобразован в DTO
     * @return объект BudgetResponseDto, созданный на основе данных из entity
     */
    @Mapping(target = "periodStart", source = "periodStart", dateFormat = "d.M.yyyy")
    @Mapping(target = "periodEnd", source = "periodEnd", dateFormat = "d.M.yyyy")
    @Mapping(target = "category", source = "categoryId")
    BudgetResponseDto toDto(Budget entity);
}
