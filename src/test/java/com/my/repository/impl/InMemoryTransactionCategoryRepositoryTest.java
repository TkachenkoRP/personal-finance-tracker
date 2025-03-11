package com.my.repository.impl;

import com.my.model.TransactionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTransactionCategoryRepositoryTest {
    private InMemoryTransactionCategoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionCategoryRepository();
    }

    private TransactionCategory createCategory(String name) {
        TransactionCategory category = new TransactionCategory(name);
        return repository.save(category);
    }

    @Test
    void testGetAll() {
        createCategory("Food");
        createCategory("Transport");
        List<TransactionCategory> categories = repository.getAll();
        assertThat(categories).hasSize(2);
    }

    @Test
    void testGetById_ExistingId() {
        TransactionCategory savedCategory = createCategory("Food");
        Optional<TransactionCategory> foundCategory = repository.getById(savedCategory.getId());
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getCategoryName()).isEqualTo("Food");
    }

    @Test
    void testGetById_NonExistingId() {
        Optional<TransactionCategory> foundCategory = repository.getById(999L);
        assertThat(foundCategory).isNotPresent();
    }

    @Test
    void testSave() {
        TransactionCategory savedCategory = createCategory("Food");
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getCategoryName()).isEqualTo("Food");
    }

    @Test
    void testUpdate() {
        TransactionCategory savedCategory = createCategory("Food");
        savedCategory.setCategoryName("Groceries");
        TransactionCategory updatedCategory = repository.update(savedCategory);
        assertThat(updatedCategory.getCategoryName()).isEqualTo("Groceries");
    }

    @Test
    void testDeleteById_ExistingId() {
        TransactionCategory savedCategory = createCategory("Food");
        boolean isDeleted = repository.deleteById(savedCategory.getId());
        assertThat(isDeleted).isTrue();
        assertThat(repository.getById(savedCategory.getId())).isNotPresent();
    }

    @Test
    void testDeleteById_NonExistingId() {
        boolean isDeleted = repository.deleteById(999L);
        assertThat(isDeleted).isFalse();
    }

    @Test
    void testExistsByCategoryNameIgnoreCase_ExistingCategory() {
        createCategory("Food");
        assertThat(repository.existsByCategoryNameIgnoreCase("food")).isTrue();
    }

    @Test
    void testExistsByCategoryNameIgnoreCase_NonExistingCategory() {
        assertThat(repository.existsByCategoryNameIgnoreCase("NonExistingCategory")).isFalse();
    }
}
