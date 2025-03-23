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
    private Long categoryId;
    private boolean active;

    public Goal(BigDecimal targetAmount, Long categoryId) {
        this.targetAmount = targetAmount;
        this.categoryId = categoryId;
        active = true;
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
               ", categoryId=" + categoryId +
               ", active=" + active +
               '}';
    }
}
