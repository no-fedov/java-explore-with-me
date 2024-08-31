package ru.practicum.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice("ru.practicum")
@Slf4j
public class ExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingURLParameter(MissingServletRequestParameterException e) {
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleNoValidRequestBodyException(final MethodArgumentNotValidException e) {
        List<String> descriptionViolations = e.getFieldErrors().stream()
                .map(x -> {
                    return x.getField() + " -> " + x.getDefaultMessage();
                })
                .collect(Collectors.toList());
        log.warn("Тело запроса содержит невалидные данные: {}.", descriptionViolations);
        return Map.of("Тело запроса содержит некорректные данные", descriptionViolations);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundEntity(RuntimeException e) {
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler(NoValidParameter.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoValidParameter(RuntimeException e) {
        return Map.of("Ошибка: ", e.getMessage());
    }

    @ExceptionHandler({EventActionException.class, RequestActionException.class,
            CategoryActionException.class, UserActionException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleClientException(RuntimeException e) {
        return Map.of("Ошибка: ", e.getMessage());
    }
}
