package ru.practicum.exception.request;

public class RequestRepeatException extends RequestException {
    public RequestRepeatException(String message) {
        super(message);
    }
}
