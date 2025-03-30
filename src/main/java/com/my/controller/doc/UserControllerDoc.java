package com.my.controller.doc;

import com.my.dto.UserRequestDto;
import com.my.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

public interface UserControllerDoc {
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все пользователи")
    })
    List<UserResponseDto> getAll();

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен пользователь"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    UserResponseDto getById(Long id);

    @Operation(summary = "Обновить пользователя", description = "Обновляет информацию о пользователе по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    UserResponseDto patch(Long id, UserRequestDto request);

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    })
    void delete(Long id);

    @Operation(summary = "Заблокировать пользователя", description = "Блокирует пользователя по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно заблокирован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    void blockUser(Long id);

    @Operation(summary = "Разблокировать пользователя", description = "Разблокирует пользователя по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно разблокирован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    void unblockUser(Long id);
}
