package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.URLParameter;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.HitService;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequiredArgsConstructor
public class HitController {

    private static final DateTimeFormatter timeFormat = DateTimeFormatter
            .ofPattern(URLDecoder.decode("yyyy-MM-dd HH:mm:ss", UTF_8));

    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody EndpointHit endpointHit) {
        hitService.saveHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStatistic(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, timeFormat);
        LocalDateTime endTime = LocalDateTime.parse(end, timeFormat);

        if (uris == null) {
            uris = List.of();
        }

        URLParameter parameters = URLParameter.builder()
                .start(startTime)
                .end(endTime)
                .uris(uris)
                .unique(unique).build();

        return hitService.getStatistic(parameters);
    }
}
