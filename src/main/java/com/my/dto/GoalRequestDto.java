package com.my.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GoalRequestDto {
    @NotNull(groups = GoalRequestDto.Post.class, message = "Поле targetAmount должно быть заполнено!")
    @DecimalMin(groups = {GoalRequestDto.Post.class, GoalRequestDto.Update.class}, value = "0.0", inclusive = false, message = "Сумма должна быть больше нуля!")
    private BigDecimal targetAmount;
    @NotNull(groups = GoalRequestDto.Post.class, message = "Поле categoryId должно быть заполнено!")
    private Long categoryId;

    public interface Post {
    }

    public interface Update {
    }
}
