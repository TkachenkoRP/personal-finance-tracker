package com.my.service.impl;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.mapper.TransactionCategoryMapper;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;
import com.my.repository.impl.JdbcTransactionCategoryRepository;
import com.my.service.TransactionCategoryService;

import java.sql.SQLException;
import java.util.List;

public class TransactionCategoryServiceImpl implements TransactionCategoryService {

    private final TransactionCategoryRepository transactionCategoryRepository;

    public TransactionCategoryServiceImpl() {
        this.transactionCategoryRepository = new JdbcTransactionCategoryRepository();
    }

    public TransactionCategoryServiceImpl(TransactionCategoryRepository transactionCategoryRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    @Override
    public List<TransactionCategoryResponseDto> getAll() throws SQLException {
        List<TransactionCategory> transactionCategoryList = transactionCategoryRepository.getAll();
        List<TransactionCategoryResponseDto> responseDtoList = transactionCategoryList.stream().map(TransactionCategoryMapper.INSTANCE::toDto).toList();
        return responseDtoList;
    }

    @Override
    public TransactionCategoryResponseDto getById(Long id) throws SQLException {
        TransactionCategory transactionCategory = transactionCategoryRepository.getById(id).orElse(null);
        TransactionCategoryResponseDto categoryResponseDto = TransactionCategoryMapper.INSTANCE.toDto(transactionCategory);
        return categoryResponseDto;
    }

    @Override
    public TransactionCategoryResponseDto save(TransactionCategoryRequestDto request) throws SQLException {
        if (transactionCategoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            return null;
        }
        TransactionCategory requestEntity = TransactionCategoryMapper.INSTANCE.toEntity(request);
        TransactionCategory saved = transactionCategoryRepository.save(requestEntity);
        TransactionCategoryResponseDto categoryResponseDto = TransactionCategoryMapper.INSTANCE.toDto(saved);
        return categoryResponseDto;
    }

    @Override
    public TransactionCategoryResponseDto update(Long id, TransactionCategoryRequestDto sourceTransactionCategory) throws SQLException {
        TransactionCategory updatedTransactionCategory = transactionCategoryRepository.getById(id).orElse(null);
        if (updatedTransactionCategory == null) {
            return null;
        }
        TransactionCategoryMapper.INSTANCE.updateTransaction(sourceTransactionCategory, updatedTransactionCategory);
        TransactionCategory updated = transactionCategoryRepository.update(updatedTransactionCategory);
        TransactionCategoryResponseDto categoryResponseDto = TransactionCategoryMapper.INSTANCE.toDto(updated);
        return categoryResponseDto;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        return transactionCategoryRepository.deleteById(id);
    }
}