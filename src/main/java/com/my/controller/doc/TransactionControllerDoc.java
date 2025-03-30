package com.my.controller.doc;

import com.my.dto.BalanceResponseDto;
import com.my.dto.ExpenseAnalysisDto;
import com.my.dto.ExpensesResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.dto.IncomeResponseDto;
import com.my.dto.PeriodFilterRequestDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.model.TransactionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

public interface TransactionControllerDoc {
    @Operation(summary = "Получить все транзакции", description = "Возвращает список всех транзакций с применением фильтров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все транзакции")
    })
    List<TransactionResponseDto> getAll(TransactionFilter filter);

    @Operation(summary = "Получить транзакцию по ID", description = "Возвращает транзакцию по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получена транзакция"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    TransactionResponseDto getById(Long id);

    @Operation(summary = "Получить баланс", description = "Возвращает текущий баланс пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен баланс")
    })
    BalanceResponseDto getBalance();

    @Operation(summary = "Получить доход", description = "Возвращает общий доход пользователя за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен общий доход")
    })
    IncomeResponseDto getIncome(PeriodFilterRequestDto filter);

    @Operation(summary = "Получить общие расходы", description = "Возвращает общие расходы пользователя за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены общие расходы")
    })
    ExpensesResponseDto getTotalExpenses(PeriodFilterRequestDto filter);

    @Operation(summary = "Получить расходы за месяц", description = "Возвращает расходы пользователя за текущий месяц")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены расходы за месяц")
    })
    ExpensesResponseDto getMonthExpenses();

    @Operation(summary = "Анализ расходов", description = "Возвращает анализ расходов по категориям за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен анализ расходов")
    })
    List<ExpenseAnalysisDto> getAnalyze(PeriodFilterRequestDto filter);

    @Operation(summary = "Сгенерировать финансовый отчет", description = "Возвращает финансовый отчет за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно сгенерирован финансовый отчет")
    })
    FullReportResponseDto getReport(PeriodFilterRequestDto filter);

    @Operation(summary = "Создать новую транзакцию", description = "Создает новую транзакцию на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Транзакция успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для создания транзакции")
    })
    TransactionResponseDto post(TransactionRequestDto request);

    @Operation(summary = "Обновить транзакцию", description = "Обновляет существующую транзакцию по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    TransactionResponseDto patch(Long id, TransactionRequestDto request);

    @Operation(summary = "Удалить транзакцию", description = "Удаляет транзакцию по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Транзакция успешно удалена")
    })
    void delete(Long id);
}
