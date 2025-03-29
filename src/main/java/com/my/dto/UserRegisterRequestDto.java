package com.my.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserRegisterRequestDto {
    @NotBlank(message = "Поле email должно быть заполнено!")
    @Email(message = "Введите корректный email!")
    private String email;
    @NotBlank(message = "Поле name должно быть заполнено!")
    private String name;
    @NotBlank(message = "Поле password должно быть заполнено!")
    @Length(min = 6, message = "Пароль должен содержать минимум {min} знаков!")
    private String password;
}
