package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CategoryDto {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;
}
