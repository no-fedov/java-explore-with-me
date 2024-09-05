package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.NoValidParameterException;

import java.util.Map;

@RestControllerAdvice("ru.practicum")
@Slf4j
public class ExceptionController {

    @ExceptionHandler(NoValidParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleNOValidParameter(RuntimeException e) {
        return Map.of("Ошибка: ", e.getMessage());
    }
}