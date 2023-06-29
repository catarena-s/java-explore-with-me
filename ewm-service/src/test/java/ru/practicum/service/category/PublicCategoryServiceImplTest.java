package ru.practicum.service.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.utils.Constants;
import ru.practicum.utils.TestInitDataUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicCategoryServiceImplTest {
    @Mock
    private CategoryRepository repository;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private List<Category> categoryList;
    private List<CategoryDto> categoryDtoList;
    final long catId = 1L;

    @BeforeEach
    void setUp() {
        categoryList = TestInitDataUtil.getCategoryList();
        categoryDtoList = categoryList.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Test
    void getCategories() {
        final int from = 0;
        final int size = 10;
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(categoryList));

        final List<CategoryDto> actualCategories = categoryService.getCategories(from, size);

        assertEquals(categoryDtoList, actualCategories);
    }

    @Test
    void getCategory() {
        final Category category = new Category(1L, "Category");
        final CategoryDto expectedDto = new CategoryDto(1L, "Category");
        when(repository.findById(anyLong())).thenReturn(Optional.of(category));

        final CategoryDto actualCategory = categoryService.getCategory(catId);

        assertEquals(expectedDto, actualCategory);
    }

    @Test
    void getCategory_ThrowException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.getCategory(catId));
        assertEquals(String.format(Constants.CATEGORY_WITH_ID_D_WAS_NOT_FOUND, catId), exception.getMessage());
    }
}