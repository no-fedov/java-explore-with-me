package ru.practicum.exception;

public class NoValidParameterException extends RuntimeException {
    public NoValidParameterException(String message) {
        super(message);
    }
}
