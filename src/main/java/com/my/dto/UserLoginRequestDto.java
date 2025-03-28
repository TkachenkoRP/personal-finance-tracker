package com.my.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {
    @NotBlank(message = "Укажите email пользователя.")
    @Email(message = "Введите корректный email.")
    private String email;
    @NotBlank(message = "Укажите пароль пользователя.")
    private String password;
}
