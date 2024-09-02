package ru.practicum.exception;

public class UserActionException extends RuntimeException {
    public UserActionException(String message) {
        super(message);
    }
}
