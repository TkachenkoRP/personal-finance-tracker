package com.my.service.impl;

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
    void testGetAll() {
        TransactionCategory category1 = new TransactionCategory();
        TransactionCategory category2 = new TransactionCategory();
        when(transactionCategoryRepository.getAll()).thenReturn(Arrays.asList(category1, category2));

        var result = transactionCategoryService.getAll();

        assertThat(result).hasSize(2);
        verify(transactionCategoryRepository).getAll();
    }

    @Test
    void testGetById_Exists() {
        TransactionCategory category = new TransactionCategory();
        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.of(category));

        var result = transactionCategoryService.getById(1L);

        assertThat(result).isEqualTo(category);
        verify(transactionCategoryRepository).getById(1L);
    }

    @Test
    void testGetById_NotExists() {
        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.empty());

        var result = transactionCategoryService.getById(1L);

        assertThat(result).isNull();
    }

    @Test
    void testSave_NewCategory() {
        TransactionCategory category = new TransactionCategory();
        category.setCategoryName("New Category");
        when(transactionCategoryRepository.existsByCategoryNameIgnoreCase("New Category")).thenReturn(false);
        when(transactionCategoryRepository.save(category)).thenReturn(category);

        var result = transactionCategoryService.save(category);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("New Category");
        verify(transactionCategoryRepository).save(category);
    }

    @Test
    void testSave_ExistingCategory() {
        TransactionCategory category = new TransactionCategory();
        category.setCategoryName("Existing Category");
        when(transactionCategoryRepository.existsByCategoryNameIgnoreCase("Existing Category")).thenReturn(true);

        var result = transactionCategoryService.save(category);

        assertThat(result).isNull();
        verify(transactionCategoryRepository, never()).save(any());
    }

    @Test
    void testUpdate_Exists() {
        TransactionCategory existingCategory = new TransactionCategory();
        existingCategory.setId(1L);
        existingCategory.setCategoryName("Old Name");
        TransactionCategory sourceCategory = new TransactionCategory();
        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.of(existingCategory));
        when(transactionCategoryRepository.update(existingCategory)).thenReturn(existingCategory);

        var result = transactionCategoryService.update(1L, sourceCategory);

        assertThat(result).isEqualTo(existingCategory);
        verify(transactionCategoryRepository).update(existingCategory);
    }

    @Test
    void testUpdate_NotExists() {
        TransactionCategory sourceCategory = new TransactionCategory();
        when(transactionCategoryRepository.getById(1L)).thenReturn(Optional.empty());

        var result = transactionCategoryService.update(1L, sourceCategory);

        assertThat(result).isNull();
        verify(transactionCategoryRepository, never()).update(any());
    }

    @Test
    void testDeleteById_Success() {
        when(transactionCategoryRepository.deleteById(1L)).thenReturn(true);

        boolean result = transactionCategoryService.deleteById(1L);

        assertThat(result).isTrue();
        verify(transactionCategoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_Failure() {
        when(transactionCategoryRepository.deleteById(1L)).thenReturn(false);

        boolean result = transactionCategoryService.deleteById(1L);

        assertThat(result).isFalse();
        verify(transactionCategoryRepository).deleteById(1L);
    }
}
