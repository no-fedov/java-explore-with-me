package ru.practicum.exception;

public class RequestActionException extends RuntimeException {
    public RequestActionException(String message) {
        super(message);
    }
}
