package ru.practicum.controller;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice("ru.practicum")
@Slf4j
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleServerException(Exception e) {
        log.debug("Получен статус 500 SERVER_ERROR {}", e.getMessage());
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingURLParameter(MissingServletRequestParameterException e) {
        log.debug("Получен статус 400 BAD_REQUEST {}", e.getMessage());
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleNoValidRequestBodyException(final MethodArgumentNotValidException e) {
        List<String> descriptionViolations = e.getFieldErrors().stream()
                .map(x -> x.getField() + " -> " + x.getDefaultMessage())
                .collect(Collectors.toList());
        log.debug("Получен статус 400 BAD_REQUEST. Тело запроса содержит невалидные данные: {}.", descriptionViolations);
        return Map.of("Тело запроса содержит некорректные данные", descriptionViolations);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundEntity(RuntimeException e) {
        log.debug("Получен статус 404 NOT_FOUND {}", e.getMessage());
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler(NoValidParameter.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoValidParameter(RuntimeException e) {
        log.debug("Получен статус 400 BAD_REQUEST {}", e.getMessage());
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler({EventActionException.class, RequestActionException.class,
            CategoryActionException.class, UserActionException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleClientException(RuntimeException e) {
        log.debug("Получен статус 409 CLIENT_ERROR {}", e.getMessage());
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleNoValidParameter(ConstraintViolationException e) {
        writeLog(e);
        return Map.of("Ошибка: ", e.getMessage());
    }

    private void writeLog(Throwable e) {
        log.debug("Exception: {}", Arrays.toString(e.getStackTrace()));
    }
}