package com.my.service.impl;

import com.my.mapper.TransactionMapper;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.repository.TransactionRepository;
import com.my.service.TransactionService;

import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAll(TransactionFilter filter) {
        List<Transaction> transactions = transactionRepository.getAll();
        if (filter == null) {
            return transactions;
        }

        return transactions.stream()
                .filter(t -> filter.userId() == null || t.getUser().getId().equals(filter.userId()))
                .filter(t -> filter.category() == null || t.getCategory().equals(filter.category()))
                .filter(t -> filter.date() == null || t.getDate().equals(filter.date()))
                .filter(t -> filter.type() == null || t.getType().equals(filter.type()))
                .toList();
    }

    @Override
    public Transaction getById(Long id) {
        return transactionRepository.getById(id).orElse(null);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Long id, Transaction sourceTransaction) {
        Transaction updatedTransaction = getById(id);
        if (updatedTransaction == null) {
            return null;
        }

        TransactionMapper.INSTANCE.updateTransaction(sourceTransaction, updatedTransaction);

        return transactionRepository.update(updatedTransaction);
    }

    @Override
    public boolean deleteById(Long id) {
        return transactionRepository.deleteById(id);
    }
}
