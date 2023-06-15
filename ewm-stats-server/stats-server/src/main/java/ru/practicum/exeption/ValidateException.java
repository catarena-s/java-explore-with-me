package ru.practicum.exeption;

public class ValidateException extends RuntimeException {

    public ValidateException(String message) {
        super(message);
    }
}