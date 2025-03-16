package com.my.service.impl;

import com.my.model.TransactionCategory;
import com.my.service.AbstractTestContainer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionCategoryServiceImplContainerTest extends AbstractTestContainer {
    final Long id = 1L;
    final String newCategoryName = "new name";
    final Long wrongId = 100L;
    TransactionCategory transactionCategoryTester;
    final Long idForUpdate = 3L;
    final String categoryNameForWrongUpdate = "Investments";
    final Long idForDelete = 4L;
    final Long newId = 5L;

    @Test
    void whenGetAllCategories_thenReturnAllCategories() throws Exception {
        List<TransactionCategory> transactionCategories = transactionCategoryService.getAll();
        assertThat(transactionCategories).hasSize(4);
    }

    @Test
    void whenFindCategoryById_thenReturnCategory() throws Exception {
        TransactionCategory transactionCategory = transactionCategoryService.getById(id);
        assertThat(transactionCategory).isNotNull();
        assertThat(transactionCategory.getId()).isEqualTo(id);
    }

    @Test
    void whenFindCategoryById_withWrongId_thenReturnNull() throws Exception {
        TransactionCategory transactionCategory = transactionCategoryService.getById(wrongId);
        assertThat(transactionCategory).isNull();
    }

    @Test
    void whenSaveCategory_theReturnNewCategory() throws Exception {
        TransactionCategory transactionCategory = transactionCategoryService.save(new TransactionCategory(newCategoryName));
        assertThat(transactionCategory).isNotNull();
        assertThat(transactionCategory.getId()).isNotNull().isEqualTo(newId);
    }

    @Test
    void whenSaveCategory_withWrongName_theReturnNull() throws Exception {
        TransactionCategory transactionCategory = transactionCategoryService.save(new TransactionCategory(categoryNameForWrongUpdate));
        assertThat(transactionCategory).isNull();
    }

    @Test
    void whenUpdateCategory_theReturnUpdatedCategory() throws Exception {
        transactionCategoryTester = transactionCategoryService.getById(idForUpdate);

        assertThat(transactionCategoryTester.getId()).isEqualTo(idForUpdate);
        assertThat(transactionCategoryTester.getCategoryName()).isEqualTo(categoryNameForWrongUpdate);

        transactionCategoryTester.setCategoryName(newCategoryName + idForUpdate);
        TransactionCategory updated = transactionCategoryService.update(idForUpdate, transactionCategoryTester);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(idForUpdate);
        assertThat(updated.getCategoryName()).isEqualTo(newCategoryName + idForUpdate);
    }

    @Test
    void whenUpdateCategory_withWrongId_thenReturnNull() throws Exception {
        transactionCategoryTester = transactionCategoryService.getById(id);
        TransactionCategory updated = transactionCategoryService.update(wrongId, transactionCategoryTester);
        assertThat(updated).isNull();
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
