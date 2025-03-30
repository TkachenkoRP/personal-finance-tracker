package com.my.service.impl;

import com.my.annotation.Loggable;
import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.TransactionCategoryException;
import com.my.mapper.TransactionCategoryMapper;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;
import com.my.service.TransactionCategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Loggable
@Service
@RequiredArgsConstructor
public class TransactionCategoryServiceImpl implements TransactionCategoryService {
    private static final Logger logger = LogManager.getRootLogger();
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionCategoryMapper transactionCategoryMapper;

    private static final String CATEGORY_NOT_FOUND = "Категория с id {0} не найдена";
    public static final String CATEGORY_ALREADY_EXISTS = "Категория с названием {0} уже существует";

    @Override
    public List<TransactionCategoryResponseDto> getAll() {
        List<TransactionCategory> transactionCategoryList = transactionCategoryRepository.getAll();
        List<TransactionCategoryResponseDto> responseDtoList = transactionCategoryMapper.toDto(transactionCategoryList);
        logger.log(Level.DEBUG, "Get all transaction categories");
        return responseDtoList;
    }

    @Override
    public TransactionCategoryResponseDto getById(Long id) {
        TransactionCategory transactionCategory = getEntityById(id);
        TransactionCategoryResponseDto categoryResponseDto = transactionCategoryMapper.toDto(transactionCategory);
        logger.log(Level.DEBUG, "Get transaction categoryId by id: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    private TransactionCategory getEntityById(Long id) {
        TransactionCategory transactionCategory = transactionCategoryRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(CATEGORY_NOT_FOUND, id))
        );
        logger.log(Level.DEBUG, "Get entity transaction category by id: {}", transactionCategory);
        return transactionCategory;
    }

    @Override
    public TransactionCategoryResponseDto save(TransactionCategoryRequestDto request) {
        if (transactionCategoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            throw new TransactionCategoryException(MessageFormat.format(CATEGORY_ALREADY_EXISTS, request.getCategoryName()));
        }
        TransactionCategory requestEntity = transactionCategoryMapper.toEntity(request);
        TransactionCategory saved = transactionCategoryRepository.save(requestEntity);
        TransactionCategoryResponseDto categoryResponseDto = transactionCategoryMapper.toDto(saved);
        logger.log(Level.DEBUG, "Save transaction category: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Override
    public TransactionCategoryResponseDto update(Long id, TransactionCategoryRequestDto sourceTransactionCategory) {
        TransactionCategory updatedTransactionCategory = getEntityById(id);
        if (transactionCategoryRepository.existsByCategoryNameIgnoreCase(sourceTransactionCategory.getCategoryName())) {
            throw new TransactionCategoryException(MessageFormat.format(CATEGORY_ALREADY_EXISTS, sourceTransactionCategory.getCategoryName()));
        }
        transactionCategoryMapper.updateTransaction(sourceTransactionCategory, updatedTransactionCategory);
        TransactionCategory updated = transactionCategoryRepository.update(updatedTransactionCategory);
        TransactionCategoryResponseDto categoryResponseDto = transactionCategoryMapper.toDto(updated);
        logger.log(Level.DEBUG, "Update transaction category: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Override
    public boolean deleteById(Long id) {
        return transactionCategoryRepository.deleteById(id);
    }
}