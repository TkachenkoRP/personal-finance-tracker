package com.my.service.impl;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.TransactionCategoryException;
import com.my.mapper.TransactionCategoryMapper;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;
import com.my.repository.impl.JdbcTransactionCategoryRepository;
import com.my.service.TransactionCategoryService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class TransactionCategoryServiceImpl implements TransactionCategoryService {
    private static final Logger logger = LogManager.getRootLogger();
    private final TransactionCategoryRepository transactionCategoryRepository;

    private static final String CATEGORY_NOT_FOUND = "Категория с id {0} не найдена";
    public static final String CATEGORY_ALREADY_EXISTS = "Категория с названием {0} уже существует";

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
        logger.log(Level.DEBUG, "Get all transaction categories");
        return responseDtoList;
    }

    @Override
    public TransactionCategoryResponseDto getById(Long id) throws SQLException {
        TransactionCategory transactionCategory = getEntityById(id);
        TransactionCategoryResponseDto categoryResponseDto = TransactionCategoryMapper.INSTANCE.toDto(transactionCategory);
        logger.log(Level.DEBUG, "Get transaction categoryId by id: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Override
    public TransactionCategory getEntityById(Long id) throws SQLException {
        TransactionCategory transactionCategory = transactionCategoryRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(CATEGORY_NOT_FOUND, id))
        );
        logger.log(Level.DEBUG, "Get entity transaction category by id: {}", transactionCategory);
        return transactionCategory;
    }

    @Override
    public TransactionCategoryResponseDto save(TransactionCategoryRequestDto request) throws SQLException {
        if (transactionCategoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            throw new TransactionCategoryException(MessageFormat.format(CATEGORY_ALREADY_EXISTS, request.getCategoryName()));
        }
        TransactionCategory requestEntity = TransactionCategoryMapper.INSTANCE.toEntity(request);
        TransactionCategory saved = transactionCategoryRepository.save(requestEntity);
        TransactionCategoryResponseDto categoryResponseDto = TransactionCategoryMapper.INSTANCE.toDto(saved);
        logger.log(Level.DEBUG, "Save transaction category: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Override
    public TransactionCategoryResponseDto update(Long id, TransactionCategoryRequestDto sourceTransactionCategory) throws SQLException {
        TransactionCategory updatedTransactionCategory = getEntityById(id);
        if (transactionCategoryRepository.existsByCategoryNameIgnoreCase(sourceTransactionCategory.getCategoryName())) {
            throw new TransactionCategoryException(MessageFormat.format(CATEGORY_ALREADY_EXISTS, sourceTransactionCategory.getCategoryName()));
        }
        TransactionCategoryMapper.INSTANCE.updateTransaction(sourceTransactionCategory, updatedTransactionCategory);
        TransactionCategory updated = transactionCategoryRepository.update(updatedTransactionCategory);
        TransactionCategoryResponseDto categoryResponseDto = TransactionCategoryMapper.INSTANCE.toDto(updated);
        logger.log(Level.DEBUG, "Update transaction category: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        return transactionCategoryRepository.deleteById(id);
    }
}