package ru.practicum.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.utils.TestValidatorUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryValidationTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "               ", "",})
    void create_NewCategoryDto_withWrongName2(String name) {
        final NewCategoryDto newCategoryDto = new NewCategoryDto(name);
        Assertions.assertTrue(TestValidatorUtil.hasErrorMessage(newCategoryDto, "Category name cannot be empty or null"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "               ", "",})
    void create_CategoryDto_withWrongName(String name) {
        final CategoryDto newCategoryDto = new CategoryDto(1L, name);
        Assertions.assertTrue(TestValidatorUtil.hasErrorMessage(newCategoryDto, "Category name cannot be empty or null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"RFkH189hRGmHGycUZvEaGa5mIAwslocA0q2akEyOX0vXm5kvePZhENy5bma9VFn4C0gWINtEXBofFI5KRFBmig"})
    void create_NewCategoryDto_withWrongSizeName(String name) {
        final NewCategoryDto newCategoryDto = new NewCategoryDto(name);
        Assertions.assertTrue(TestValidatorUtil.hasErrorMessage(newCategoryDto, "size must be between 0 and 50"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"RFkH189hRGmHGycUZvEaGa5mIAwslocA0q2akEyOX0vXm5kvePZhENy5bma9VFn4C0gWINtEXBofFI5KRFBmig"})
    void create_CategoryDto_withWrongSizeName(String name) {
        final CategoryDto newCategoryDto = new CategoryDto(1L, name);
        Assertions.assertTrue(TestValidatorUtil.hasErrorMessage(newCategoryDto, "size must be between 1 and 50"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"5re5MG0VGrN6w5ItUfBVXbQkFdzsEQGu3m7RPzUXQ0DqEgbcdw"})
    void create_NewCategoryDto_withCorrectSizeName_50(String name) {
        final NewCategoryDto newCategoryDto = new NewCategoryDto(name);
        assertEquals(name, newCategoryDto.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"5re5MG0VGrN6w5ItUfBVXbQkFdzsEQGu3m7RPzUXQ0DqEgbcdw"})
    void create_CategoryDto_withCorrectSizeName_50(String name) {
        final CategoryDto newCategoryDto = new CategoryDto(1L, name);
        assertEquals(name, newCategoryDto.getName());
    }
}
