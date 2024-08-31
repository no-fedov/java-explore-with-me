package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class URLParameterEventPublic {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private Pageable page;
    private SortType sort;
}

