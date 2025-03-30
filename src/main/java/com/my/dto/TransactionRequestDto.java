package com.my.dto;

import com.my.annotation.ValueOfEnum;
import com.my.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldNameConstants
public class TransactionRequestDto {
    @NotBlank(groups = Post.class, message = "Поле date должно быть заполнено!")
    private String date;
    @NotBlank(groups = Post.class, message = "Поле type должно быть заполнено!")
    @ValueOfEnum(groups = {Post.class, Update.class}, enumClass = TransactionType.class)
    private String type;
    @NotNull(groups = Post.class, message = "Поле amount должно быть заполнено!")
    @DecimalMin(groups = {Post.class, Update.class}, value = "0.0", inclusive = false, message = "Сумма должна быть больше нуля!")
    private BigDecimal amount;
    @NotBlank(groups = Post.class, message = "Поле description должно быть заполнено!")
    private String description;
    @NotNull(groups = Post.class, message = "Поле categoryId должно быть заполнено!")
    private Long categoryId;

    public interface Post{}
    public interface Update{}
}
