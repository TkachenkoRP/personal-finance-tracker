package com.my.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Goal {
    private Long id;
    private BigDecimal targetAmount;
    private TransactionCategory category;
    private boolean isActive;

    public Goal(BigDecimal targetAmount, TransactionCategory category) {
        this.targetAmount = targetAmount;
        this.category = category;
        isActive = true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Goal{" +
               "id=" + id +
               ", targetAmount=" + targetAmount +
               ", category=" + category.getCategoryName() +
               ", isActive=" + isActive +
               '}';
    }
}
