package com.my.service.impl;

import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.exception.EntityNotFoundException;
import com.my.exception.TransactionCategoryException;
import com.my.service.AbstractTestContainer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionCategoryServiceImplContainerTest extends AbstractTestContainer {
    final Long id = 1L;
    final String newCategoryName = "new name";
    final Long wrongId = 100L;
    TransactionCategoryResponseDto transactionCategoryTester;
    final Long idForUpdate = 3L;
    final String categoryNameForWrongUpdate = "Investments";
    final Long idForDelete = 4L;
    final Long newId = 5L;

    @Test
    void whenGetAllCategories_thenReturnAllCategories() throws Exception {
        List<TransactionCategoryResponseDto> transactionCategories = transactionCategoryService.getAll();
        assertThat(transactionCategories).hasSize(4);
    }

    @Test
    void whenFindCategoryById_thenReturnCategory() throws Exception {
        TransactionCategoryResponseDto transactionCategory = transactionCategoryService.getById(id);
        assertThat(transactionCategory).isNotNull();
        assertThat(transactionCategory.getId()).isEqualTo(id);
    }

    @Test
    void whenFindCategoryById_withWrongId_thenReturnNull() {
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> transactionCategoryService.getById(wrongId));
        assertThat(thrown.getMessage()).isEqualTo("Категория с id 100 не найдена");
    }

    @Test
    void whenSaveCategory_theReturnNewCategory() throws Exception {
        TransactionCategoryResponseDto transactionCategory = transactionCategoryService.save(new TransactionCategoryRequestDto(newCategoryName));
        assertThat(transactionCategory).isNotNull();
        assertThat(transactionCategory.getId()).isNotNull().isEqualTo(newId);
    }

    @Test
    void whenSaveCategory_withExistingName_thenThrowTransactionCategoryException() {
        TransactionCategoryException thrown = assertThrows(TransactionCategoryException.class, () -> {
            transactionCategoryService.save(new TransactionCategoryRequestDto(categoryNameForWrongUpdate));
        });
        assertThat(thrown.getMessage()).isEqualTo("Категория с названием Investments уже существует");
    }

    @Test
    void whenUpdateCategory_theReturnUpdatedCategory() throws Exception {
        transactionCategoryTester = transactionCategoryService.getById(idForUpdate);

        assertThat(transactionCategoryTester.getId()).isEqualTo(idForUpdate);
        assertThat(transactionCategoryTester.getCategoryName()).isEqualTo(categoryNameForWrongUpdate);

        transactionCategoryTester.setCategoryName(newCategoryName + idForUpdate);
        TransactionCategoryResponseDto updated = transactionCategoryService.update(idForUpdate, new TransactionCategoryRequestDto(transactionCategoryTester.getCategoryName()));

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(idForUpdate);
        assertThat(updated.getCategoryName()).isEqualTo(newCategoryName + idForUpdate);
    }

    @Test
    void whenUpdateCategory_withWrongId_thenReturnNull() throws Exception {
        transactionCategoryTester = transactionCategoryService.getById(id);
        assertThrows(EntityNotFoundException.class, () -> transactionCategoryService.update(wrongId, new TransactionCategoryRequestDto(transactionCategoryTester.getCategoryName())));
    }

    @Test
    void whenDeleteCategory_thenReturnTrue() throws Exception {
        int count = transactionCategoryRepository.getAll().size();
        boolean result = transactionCategoryService.deleteById(idForDelete);
        assertThat(result).isTrue();
        assertThat(transactionCategoryRepository.getAll()).hasSize(count - 1);
    }

    @Test
    void whenDeleteCategory_withWrongId_thenReturnFalse() throws Exception {
        int count = transactionCategoryRepository.getAll().size();
        boolean result = transactionCategoryService.deleteById(wrongId);
        assertThat(result).isFalse();
        assertThat(transactionCategoryRepository.getAll()).hasSize(count);
    }
}
