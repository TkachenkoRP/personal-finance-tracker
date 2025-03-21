package com.my.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Budget {
    private Long id;
    private BigDecimal totalAmount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private TransactionCategory category;
    private boolean isActive;

    public Budget(BigDecimal totalAmount, LocalDate periodStart, LocalDate periodEnd, TransactionCategory category) {
        this.totalAmount = totalAmount;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.category = category;
        isActive = true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(id, budget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Budget{" +
               "id=" + id +
               ", totalAmount=" + totalAmount +
               ", periodStart=" + periodStart +
               ", periodEnd=" + periodEnd +
               ", category=" + category.getCategoryName() +
               ", isActive=" + isActive +
               '}';
    }
}
