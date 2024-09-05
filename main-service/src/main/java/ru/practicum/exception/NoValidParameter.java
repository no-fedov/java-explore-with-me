package ru.practicum.exception;

public class NoValidParameter extends RuntimeException {
    public NoValidParameter(String message) {
        super(message);
    }
}
