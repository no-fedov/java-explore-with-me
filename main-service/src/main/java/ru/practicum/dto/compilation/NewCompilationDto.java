package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private String description;
    private List<Long> events = List.of();
    private Boolean pinned = false;
    @Size(min = 1, max = 50)
    private String title;
}
