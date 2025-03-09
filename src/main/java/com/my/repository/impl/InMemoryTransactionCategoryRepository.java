package com.my.repository.impl;

import com.my.mapper.TransactionCategoryMapper;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTransactionCategoryRepository implements TransactionCategoryRepository {

    private final Map<Long, TransactionCategory> repository = new ConcurrentHashMap<>();

    private final AtomicLong currentId = new AtomicLong(1);

    @Override
    public List<TransactionCategory> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public Optional<TransactionCategory> getById(Long id) {
        TransactionCategory transactionCategory = repository.get(id);
        if (transactionCategory == null) {
            return Optional.empty();
        }
        return Optional.of(TransactionCategoryMapper.INSTANCE.copyTransactionCategory(transactionCategory));
    }

    @Override
    public TransactionCategory save(TransactionCategory transactionCategory) {
        Long id = currentId.getAndIncrement();
        transactionCategory.setId(id);
        repository.put(id, transactionCategory);
        return transactionCategory;
    }

    @Override
    public TransactionCategory update(TransactionCategory transactionCategory) {
        repository.put(transactionCategory.getId(), transactionCategory);
        return transactionCategory;
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
