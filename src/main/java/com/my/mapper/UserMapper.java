package com.my.mapper;

import com.my.dto.UserRegisterRequestDto;
import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import com.my.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Интерфейс UserMapper для преобразования объектов типа User.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    /**
     * Экземпляр UserMapper для использования в приложении.
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    void updateUser(UserRequestDto sourceUser, @MappingTarget User targetUser);

    User toEntity(UserRegisterRequestDto request);

    User toEntity(Long id, UserRequestDto request);

    UserResponseDto toDto(User entity);
}
