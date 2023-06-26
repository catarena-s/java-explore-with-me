package ru.practicum.service.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.utils.Constants;
import ru.practicum.utils.TestInitDataUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.utils.Constants.CATEGORY_WITH_ID_D_WAS_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceImplTest {
    @Mock
    private CategoryRepository repository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private NewCategoryDto body;
    private CategoryDto dto;
    private Category category;
    private final long catId = 1L;
    private CategoryDto newCategoryDto;
    private Category newCategory;

    @BeforeEach
    void setUp() {
        body = NewCategoryDto.builder().name("Category").build();
        dto = CategoryDto.builder().id(1L).name("Category").build();
        category = TestInitDataUtil.makeCategory(1L, "Category");

        newCategoryDto = new CategoryDto(1L, "NewCategory");
        newCategory = new Category(1L, "NewCategory");
    }

    @Test
    void addCategory() {
        when(repository.save(any(Category.class))).thenReturn(category);

        final CategoryDto actualNewUser = categoryService.saveCategory(body);
        assertEquals(dto, actualNewUser);
    }

    @Test
    void addCategory_ThrowException() {
        when(repository.save(any())).thenThrow(DataIntegrityViolationException.class);

        final ConflictException asserted =
                assertThrows(ConflictException.class, () -> categoryService.saveCategory(body));

        assertEquals("Category with name='Category' already exists", asserted.getMessage());
    }

    @Test
    void delete() {
        when(repository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsByCategoryId(anyLong())).thenReturn(false);
        doNothing().when(repository).deleteById(anyLong());

        categoryService.delete(catId);

        verify(repository, times(1)).deleteById(catId);
        verify(repository, times(1)).existsById(catId);
        verify(eventRepository, times(1)).existsByCategoryId(catId);
    }

    @Test
    void delete_whenUserNotExists_throwException() {
        when(repository.existsById(anyLong())).thenReturn(false);

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> categoryService.delete(catId));

        assertEquals(String.format(CATEGORY_WITH_ID_D_WAS_NOT_FOUND, catId), actualException.getMessage());

        verify(repository, never()).deleteById(catId);
        verify(repository, times(1)).existsById(catId);
        verify(eventRepository, never()).existsByCategoryId(catId);
    }

    @Test
    void delete_whenCategoryNotEmpty_throwException() {
        when(repository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsByCategoryId(anyLong())).thenReturn(true);

        final ConflictException actualException = assertThrows(ConflictException.class,
                () -> categoryService.delete(catId));

        assertEquals(String.format("The category id=%d is not empty", catId), actualException.getMessage());

        verify(repository, never()).deleteById(catId);
        verify(repository, times(1)).existsById(catId);
        verify(eventRepository, times(1)).existsByCategoryId(catId);
    }

    @Test
    void updateCategory() {
        when(repository.existsByIdNotAndName(anyLong(), anyString())).thenReturn(false);
        when(repository.findById(anyLong())).thenReturn(Optional.of(category));
        when(repository.save(any())).thenReturn(newCategory);

        final CategoryDto actualCategory = categoryService.updateCategory(newCategoryDto, catId);
        assertEquals(newCategoryDto, actualCategory);

        verify(repository, times(1)).existsByIdNotAndName(catId, "NewCategory");
        verify(repository, times(1)).findById(catId);
        verify(repository, times(1)).save(newCategory);
    }

    @Test
    void updateCategory_whenNameAlreadyExist() {
        when(repository.existsByIdNotAndName(anyLong(), anyString())).thenReturn(true);

        final ConflictException conflictException = assertThrows(ConflictException.class,
                () -> categoryService.updateCategory(newCategoryDto, catId));
        assertEquals(String.format("Another category already exists with name = '%s'", newCategoryDto.getName()),
                conflictException.getMessage());

        verify(repository, times(1)).existsByIdNotAndName(catId, "NewCategory");
        verify(repository, never()).findById(catId);
        verify(repository, never()).save(newCategory);
    }

    @Test
    void updateCategory_whenCategoryNotExist() {
        when(repository.existsByIdNotAndName(anyLong(), anyString())).thenReturn(false);
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException conflictException = assertThrows(NotFoundException.class,
                () -> categoryService.updateCategory(newCategoryDto, catId));
        assertEquals(String.format(Constants.CATEGORY_WITH_ID_D_WAS_NOT_FOUND, catId),
                conflictException.getMessage());

        verify(repository, times(1)).existsByIdNotAndName(catId, "NewCategory");
        verify(repository, times(1)).findById(catId);
        verify(repository, never()).save(newCategory);
    }
}