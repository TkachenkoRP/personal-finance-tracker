package com.my.controller.doc;

import com.my.dto.GoalRequestDto;
import com.my.dto.GoalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

public interface GoalControllerDoc {
    @Operation(summary = "Получить все цели", description = "Возвращает список всех целей текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все цели")
    })
    List<GoalResponseDto> getAll();

    @Operation(summary = "Получить цель по ID", description = "Возвращает цель по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получена цель"),
            @ApiResponse(responseCode = "404", description = "Цель не найдена")
    })
    GoalResponseDto getById(Long id);

    @Operation(summary = "Создать новую цель", description = "Создает новую цель на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Цель успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для создания цели")
    })
    GoalResponseDto post(GoalRequestDto request);

    @Operation(summary = "Обновить цель", description = "Обновляет существующую цель по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цель успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Цель не найдена")
    })
    GoalResponseDto patch(Long id, GoalRequestDto request);

    @Operation(summary = "Удалить цель", description = "Удаляет цель по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Цель успешно удалена")
    })
    void delete(Long id);
}
