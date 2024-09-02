package ru.practicum.controller.publiсapi;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.PageConstructor;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationServer;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
@Slf4j
public class CompilationPublicController {
    private final CompilationServer compilationServer;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        Pageable page = PageConstructor.getPage(from, size);
        return compilationServer.getCompilations(pinned, page);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("GET /compilations/{compId} compId = {}", compId);
        return compilationServer.findCompilationById(compId);
    }
}
