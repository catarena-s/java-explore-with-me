package ru.practicum.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.utils.TestValidatorUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilationValidationTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "               ", "",})
    void create_NewCategoryDto_withWrongTitle(String name) {
        final NewCompilationDto newCompilationDto = NewCompilationDto.builder().title(name).build();
        Assertions.assertTrue(TestValidatorUtil.hasErrorMessage(newCompilationDto, "Title cannot be null or empty"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"RFkH189hRGmHGycUZvEaGa5mIAwslocA0q2akEyOX0vXm5kvePZhENy5bma9VFn4C0gWINtEXBofFI5KRFBmig"})
    void create_NewCompilationDto_withWrongSizeTitle(String name) {
        final NewCompilationDto newCompilationDto = NewCompilationDto.builder().title(name).build();
        Assertions.assertTrue(TestValidatorUtil.hasErrorMessage(newCompilationDto, "size must be between 1 and 50"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"RFkH189hRGmHGycUZvEaGa5mIAwslocA0q2akEyOX0vXm5kvePZhENy5bma9VFn4C0gWINtEXBofFI5KRFBmig"})
    void create_UpdateCompilationRequest_withWrongSizeTitle(String name) {
        final UpdateCompilationRequest newCompilationDto = UpdateCompilationRequest.builder().title(name).build();
        Assertions.assertTrue(TestValidatorUtil.hasErrorMessage(newCompilationDto, "size must be between 1 and 50"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"5re5MG0VGrN6w5ItUfBVXbQkFdzsEQGu3m7RPzUXQ0DqEgbcdw"})
    void create_NewCompilationDto_withSize50(String name) {
        final NewCompilationDto newCompilationDto = NewCompilationDto.builder().title(name).build();
        assertEquals(name, newCompilationDto.getTitle());
    }

    @ParameterizedTest
    @ValueSource(strings = {"5re5MG0VGrN6w5ItUfBVXbQkFdzsEQGu3m7RPzUXQ0DqEgbcdw"})
    void create_UpdateCompilationRequest_withSize50(String name) {
        final UpdateCompilationRequest newCompilationDto = UpdateCompilationRequest.builder().title(name).build();
        assertEquals(name, newCompilationDto.getTitle());
    }


}
