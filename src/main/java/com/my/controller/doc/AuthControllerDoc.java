package com.my.controller.doc;

import com.my.dto.UserLoginRequestDto;
import com.my.dto.UserRegisterRequestDto;
import com.my.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface AuthControllerDoc {
    @Operation(summary = "Вход пользователя", description = "Аутентификация пользователя по email и паролю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    UserResponseDto login(UserLoginRequestDto request);

    @Operation(summary = "Выход пользователя", description = "Выход текущего аутентифицированного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход")
    })
    void logout();

    @Operation(summary = "Регистрация пользователя", description = "Регистрация нового пользователя с email, именем и паролем")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная регистрация"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    UserResponseDto register(UserRegisterRequestDto request);
}
