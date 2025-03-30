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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Loggable
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCategoryServiceImpl implements TransactionCategoryService {
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionCategoryMapper transactionCategoryMapper;

    private static final String CATEGORY_NOT_FOUND = "Категория с id {0} не найдена";
    public static final String CATEGORY_ALREADY_EXISTS = "Категория с названием {0} уже существует";

    @Override
    public List<TransactionCategoryResponseDto> getAll() {
        List<TransactionCategory> transactionCategoryList = transactionCategoryRepository.getAll();
        List<TransactionCategoryResponseDto> responseDtoList = transactionCategoryMapper.toDto(transactionCategoryList);
        log.debug("Get all transaction categories");
        return responseDtoList;
    }

    @Override
    public TransactionCategoryResponseDto getById(Long id) {
        TransactionCategory transactionCategory = getEntityById(id);
        TransactionCategoryResponseDto categoryResponseDto = transactionCategoryMapper.toDto(transactionCategory);
        log.debug("Get transaction categoryId by id: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    private TransactionCategory getEntityById(Long id) {
        TransactionCategory transactionCategory = transactionCategoryRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format(CATEGORY_NOT_FOUND, id))
        );
        log.debug("Get entity transaction category by id: {}", transactionCategory);
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
        log.debug("Save transaction category: {}", categoryResponseDto);
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
        log.debug("Update transaction category: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Override
    public boolean deleteById(Long id) {
        return transactionCategoryRepository.deleteById(id);
    }
}