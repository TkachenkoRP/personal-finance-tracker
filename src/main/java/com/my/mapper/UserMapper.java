package com.my.mapper;

import com.my.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Интерфейс UserMapper для преобразования объектов типа User.
 * Использует MapStruct для автоматизации процесса маппинга.
 */
@Mapper
public interface UserMapper {
    /**
     * Экземпляр UserMapper для использования в приложении.
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Обновляет существующий объект User, копируя значения из другого объекта User.
     *
     * @param sourceUser   объект User, из которого будут скопированы значения
     * @param targetUser   объект User, который будет обновлен
     */
    @Mapping(target = "id", ignore = true)
    void updateUser(User sourceUser, @MappingTarget User targetUser);

    /**
     * Копирует данные из одного объекта User в новый объект User.
     *
     * @param sourceUser объект User, из которого будут скопированы значения
     * @return новый объект User с данными из sourceUser
     */
    User copyUser(User sourceUser);
}
