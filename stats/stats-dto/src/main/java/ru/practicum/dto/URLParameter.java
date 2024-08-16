package ru.practicum.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class URLParameter {
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private List<String> uris;
    private Boolean unique;
}
