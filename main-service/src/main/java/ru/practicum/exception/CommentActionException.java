package ru.practicum.exception;

public class CommentActionException extends RuntimeException {
    public CommentActionException(String message) {
        super(message);
    }
}
