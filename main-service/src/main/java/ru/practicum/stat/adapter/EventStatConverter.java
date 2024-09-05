package ru.practicum.stat.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.event.EventFullDto;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventStatConverter {
    public static List<EventFullDto> convertToEventWithStatistic(List<EventFullDto> events,
                                                                 Map<Long, ViewStats> stats) {
        for (EventFullDto event : events) {
            ViewStats statistic = stats.get(event.getId());
            if (statistic == null) {
                event.setViews(0L);
                continue;
            }
            Long views = statistic.getHits();
            event.setViews(views);
        }
        return events;
    }
}
