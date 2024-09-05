package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    private boolean pinned;
    private String title;
}
