package ru.practicum.service.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.utils.Constants;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository repository;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private final long catId = 1L;

    @Test
    void findCategoryById() {
        Category expectedCategory = new Category(catId, "Category");
        when(repository.findById(anyLong())).thenReturn(Optional.of(expectedCategory));

        final Category actualCategory = categoryService.findCategoryById(catId);
        assertEquals(expectedCategory, actualCategory);

        verify(repository, times(1)).findById(catId);
    }

    @Test
    void findCategoryById_whenNotExist() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.findCategoryById(catId));
        assertEquals(String.format(Constants.CATEGORY_WITH_ID_D_WAS_NOT_FOUND, catId), exception.getMessage());

        verify(repository, times(1)).findById(catId);
    }
}