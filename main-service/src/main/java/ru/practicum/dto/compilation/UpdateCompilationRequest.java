package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    @Builder.Default
    private List<Long> events = new ArrayList<>();
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
