package com.my.mapper;

import com.my.dto.UserRegisterRequestDto;
import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Интерфейс UserMapper для преобразования объектов типа User.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    /**
     * Обновляет существующий объект User на основе данных из UserRequestDto.
     *
     * @param sourceUser объект UserRequestDto, содержащий данные для обновления
     * @param targetUser объект User, который будет обновлен
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserRequestDto sourceUser, @MappingTarget User targetUser);

    /**
     * Преобразует объект UserRegisterRequestDto в сущность User.
     *
     * @param request объект UserRegisterRequestDto, содержащий данные для создания пользователя
     * @return объект User, созданный на основе данных из request
     */
    User toEntity(UserRegisterRequestDto request);

    /**
     * Преобразует сущность User в объект UserResponseDto.
     *
     * @param entity объект User, который будет преобразован в DTO
     * @return объект UserResponseDto, созданный на основе данных из entity
     */
    UserResponseDto toDto(User entity);

    /**
     * Преобразует список сущностей User в список объектов UserResponseDto.
     *
     * @param entities список объектов User, которые будут преобразованы в DTO
     * @return список объектов UserResponseDto, созданный на основе данных из entities
     */
    List<UserResponseDto> toDto(List<User> entities);
}
