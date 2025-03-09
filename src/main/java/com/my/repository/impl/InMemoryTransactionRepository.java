package com.my.repository.impl;

import com.my.mapper.TransactionMapper;
import com.my.model.Transaction;
import com.my.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<Long, Transaction> repository = new ConcurrentHashMap<>();

    private final AtomicLong currentId = new AtomicLong(1);

    @Override
    public List<Transaction> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public Optional<Transaction> getById(Long id) {
        Transaction transaction = repository.get(id);
        if (transaction == null) {
            return Optional.empty();
        }
        return Optional.of(TransactionMapper.INSTANCE.copyTransaction(transaction));
    }

    @Override
    public Transaction save(Transaction transaction) {
        Long id = currentId.getAndIncrement();
        transaction.setId(id);
        repository.put(id, transaction);
        return transaction;
    }

    @Override
    public Transaction update(Transaction transaction) {
        repository.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public boolean deleteById(Long id) {
        if (getById(id).isPresent()) {
            repository.remove(id);
            return true;
        }
        return false;
    }
}
