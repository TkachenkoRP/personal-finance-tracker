package com.my.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class BudgetRequestDto {
    @NotNull(groups = BudgetRequestDto.Post.class, message = "Поле totalAmount должно быть заполнено!")
    @DecimalMin(groups = {BudgetRequestDto.Post.class, BudgetRequestDto.Update.class}, value = "0.0", inclusive = false, message = "Сумма должна быть больше нуля!")
    private BigDecimal totalAmount;
    @NotBlank(groups = BudgetRequestDto.Post.class, message = "Поле periodStart должно быть заполнено!")
    private String periodStart;
    @NotBlank(groups = BudgetRequestDto.Post.class, message = "Поле periodEnd должно быть заполнено!")
    private String periodEnd;
    @NotNull(groups = BudgetRequestDto.Post.class, message = "Поле categoryId должно быть заполнено!")
    private Long categoryId;

    public interface Post{}
    public interface Update{}
}
