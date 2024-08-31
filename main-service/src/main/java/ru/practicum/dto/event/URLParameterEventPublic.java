package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import ru.practicum.exception.NoValidParameter;

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

    public void checkValid() {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new NoValidParameter("Невалидные данные");
            }
        }
    }
}

