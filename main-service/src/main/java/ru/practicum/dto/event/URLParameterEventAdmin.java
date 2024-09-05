package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import ru.practicum.exception.NoValidParameter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class URLParameterEventAdmin {
    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Pageable page;

    public void checkValid() {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new NoValidParameter("Невалидные данные");
            }
        }
    }
}
