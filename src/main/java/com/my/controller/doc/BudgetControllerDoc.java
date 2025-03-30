package com.my.controller.doc;

import com.my.dto.BudgetRequestDto;
import com.my.dto.BudgetResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

public interface BudgetControllerDoc {
    @Operation(summary = "Получить все бюджеты", description = "Возвращает список всех бюджетов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все бюджеты")
    })
    List<BudgetResponseDto> getAll();

    @Operation(summary = "Получить бюджет по ID", description = "Возвращает бюджет по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен бюджет"),
            @ApiResponse(responseCode = "404", description = "Бюджет не найден")
    })
    BudgetResponseDto getById(Long id);

    @Operation(summary = "Получить бюджеты по ID пользователя", description = "Возвращает список бюджетов для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены бюджеты для пользователя"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    List<BudgetResponseDto> getByUserId(Long userId);

    @Operation(summary = "Создать новый бюджет", description = "Создает новый бюджет на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Бюджет успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для создания бюджета")
    })
    BudgetResponseDto post(BudgetRequestDto request);

    @Operation(summary = "Обновить бюджет", description = "Обновляет существующий бюджет по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бюджет успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Бюджет не найден")
    })
    BudgetResponseDto patch(Long id, BudgetRequestDto request);

    @Operation(summary = "Удалить бюджет", description = "Удаляет бюджет по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Бюджет успешно удален")
    })
    void delete(Long id);
}
