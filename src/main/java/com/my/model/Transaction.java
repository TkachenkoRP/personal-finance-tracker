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
public class Transaction {
    private Long id;
    private LocalDate date;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private TransactionCategory category;
    private User user;

    public Transaction(LocalDate date, TransactionType type, BigDecimal amount, String description, TransactionCategory category) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", date=" + date +
                ", type=" + type +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category=" + category.getCategoryName() +
                '}';
    }
}
