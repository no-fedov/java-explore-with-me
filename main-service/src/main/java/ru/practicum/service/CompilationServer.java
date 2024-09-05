package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationServer {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto findCompilationById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Pageable page);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
