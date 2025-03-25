package com.my.service.impl;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.TransactionCategoryException;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionCategoryServiceImplTest {

    @Mock
    private TransactionCategoryRepository transactionCategoryRepository;

    @InjectMocks
    private TransactionCategoryServiceImpl transactionCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() throws Exception {
        TransactionCategory category1 = new TransactionCategory();
        TransactionCategory category2 = new TransactionCategory();
        when(transactionCategoryRepository.getAll()).thenReturn(Arrays.asList(category1, category2));

        var result = transactionCategoryService.getAll();

        assertThat(result).hasSize(2);
        verify(transactionCategoryRepository).getAll();
    }

    @Test
    void testGetById_Exists() throws Exception {
        TransactionCategory category = new TransactionCategory();
        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.of(category));

        var result = transactionCategoryService.getById(1L);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(TransactionCategoryResponseDto.class);
        verify(transactionCategoryRepository).getById(1L);
    }

    @Test
    void testGetById_NotExists() throws Exception {
        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class,
                () -> transactionCategoryService.getById(1L));

        assertThat(thrown.getMessage()).contains("Категория с id 1 не найдена");
    }

    @Test
    void testSave_NewCategory() throws Exception {
        TransactionCategoryRequestDto requestDto = new TransactionCategoryRequestDto();
        requestDto.setCategoryName("New Category");
        when(transactionCategoryRepository.existsByCategoryNameIgnoreCase("New Category")).thenReturn(false);

        TransactionCategory savedCategory = new TransactionCategory("New Category");
        when(transactionCategoryRepository.save(any(TransactionCategory.class))).thenReturn(savedCategory);

        var result = transactionCategoryService.save(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("New Category");
        verify(transactionCategoryRepository).save(any());
    }

    @Test
    void testSave_ExistingCategory() throws Exception {
        TransactionCategoryRequestDto requestDto = new TransactionCategoryRequestDto();
        requestDto.setCategoryName("Existing Category");
        when(transactionCategoryRepository.existsByCategoryNameIgnoreCase("Existing Category")).thenReturn(true);

        TransactionCategoryException thrown = org.junit.jupiter.api.Assertions.assertThrows(TransactionCategoryException.class,
                () -> transactionCategoryService.save(requestDto));

        assertThat(thrown.getMessage()).contains("Категория с названием Existing Category уже существует");
        verify(transactionCategoryRepository, never()).save(any());
    }

    @Test
    void testUpdate_Exists() throws Exception {
        TransactionCategory existingCategory = new TransactionCategory();
        existingCategory.setId(1L);
        existingCategory.setCategoryName("Old Name");

        TransactionCategoryRequestDto sourceCategory = new TransactionCategoryRequestDto();
        sourceCategory.setCategoryName("New Name");

        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.of(existingCategory));
        when(transactionCategoryRepository.existsByCategoryNameIgnoreCase("New Name")).thenReturn(false);
        when(transactionCategoryRepository.update(existingCategory)).thenReturn(existingCategory);

        var result = transactionCategoryService.update(1L, sourceCategory);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("New Name");
        verify(transactionCategoryRepository).update(existingCategory);
    }

    @Test
    void testUpdate_NotExists() throws Exception {
        TransactionCategoryRequestDto sourceCategory = new TransactionCategoryRequestDto();
        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class,
                () -> transactionCategoryService.update(1L, sourceCategory));

        assertThat(thrown.getMessage()).contains("Категория с id 1 не найдена");
        verify(transactionCategoryRepository, never()).update(any());
    }

    @Test
    void testDeleteById_Success() throws Exception {
        when(transactionCategoryRepository.deleteById(1L)).thenReturn(true);

        boolean result = transactionCategoryService.deleteById(1L);

        assertThat(result).isTrue();
        verify(transactionCategoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_Failure() throws Exception {
        when(transactionCategoryRepository.deleteById(1L)).thenReturn(false);

        boolean result = transactionCategoryService.deleteById(1L);

        assertThat(result).isFalse();
        verify(transactionCategoryRepository).deleteById(1L);
    }
}
