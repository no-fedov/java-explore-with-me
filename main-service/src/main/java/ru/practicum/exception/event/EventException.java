package ru.practicum.exception.event;

public abstract class EventException extends RuntimeException {
    public EventException(String message) {
        super(message);
    }
}
