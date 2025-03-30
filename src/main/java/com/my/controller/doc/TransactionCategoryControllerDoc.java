package com.my.controller.doc;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

public interface TransactionCategoryControllerDoc {
    @Operation(summary = "Получить все категории транзакций", description = "Возвращает список всех категорий транзакций")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все категории транзакций")
    })
    List<TransactionCategoryResponseDto> getAll();

    @Operation(summary = "Получить категорию транзакции по ID", description = "Возвращает категорию транзакции по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получена категория транзакции"),
            @ApiResponse(responseCode = "404", description = "Категория транзакции не найдена")
    })
    TransactionCategoryResponseDto getById(Long id);

    @Operation(summary = "Создать новую категорию транзакции", description = "Создает новую категорию транзакции на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Категория транзакции успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для создания категории транзакции")
    })
    TransactionCategoryResponseDto post(TransactionCategoryRequestDto request);

    @Operation(summary = "Обновить категорию транзакции", description = "Обновляет существующую категорию транзакции по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория транзакции успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Категория транзакции не найдена")
    })
    TransactionCategoryResponseDto patch(Long id, TransactionCategoryRequestDto request);

    @Operation(summary = "Удалить категорию транзакции", description = "Удаляет категорию транзакции по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Категория транзакции успешно удалена")
    })
    void delete(Long id);
}
