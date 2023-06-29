package ru.practicum.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.model.Category;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository repository;

    @Test
    void existsByIdNotAndName() {
        repository.save(Category.builder().name("Category1").build());
        repository.save(Category.builder().name("Category2").build());
        repository.save(Category.builder().name("Category3").build());

        final boolean exists = repository.existsByIdNotAndName(5L, "Category5");
        assertFalse(exists);
    }

    @Test
    void existsByIdNotAndName_False() {
        repository.save(Category.builder().name("Category1").build());
        repository.save(Category.builder().name("Category2").build());
        repository.save(Category.builder().name("Category3").build());

        final boolean exists = repository.existsByIdNotAndName(5L, "Category1");
        assertTrue(exists);
    }

}