package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    @NotBlank(message = "Имя приложения не может быть пустым.")
    private String app;

    @NotBlank(message = "Адресс обращения не можеть быть пустым.")
    private String uri;

    @NotBlank(message = "IP адресс пользователя не может быть пустым.")
    private String ip;

    @NotNull(message = "Время регистрации обращения по эндпоинту не может быть пустым.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}