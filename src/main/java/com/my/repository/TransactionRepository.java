package com.my.repository;

import com.my.model.Transaction;

/**
 * Интерфейс репозитория для управления транзакциями.
 * Наследует базовый интерфейс {@link BaseRepository} для выполнения основных операций
 * с транзакциями.
 */
public interface TransactionRepository extends BaseRepository<Transaction> {
}
