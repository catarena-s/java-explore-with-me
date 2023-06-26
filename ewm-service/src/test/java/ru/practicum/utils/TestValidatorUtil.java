package ru.practicum.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestValidatorUtil {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> boolean hasErrorMessage(T obj, @NotNull String message) {
        Set<ConstraintViolation<T>> errors = VALIDATOR.validate(obj);
        return errors.stream().map(ConstraintViolation::getMessage).anyMatch(message::equals);
    }
}
