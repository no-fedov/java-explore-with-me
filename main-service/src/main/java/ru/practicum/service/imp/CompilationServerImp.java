package ru.practicum.service.imp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.mapper.CompilationMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class CompilationServerImp implements CompilationServer {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> currentEvents = null;

        if (newCompilationDto.getEvents() != null) {
            currentEvents = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        }

        Compilation newCompilation = CompilationMapper.convertFromNewCompilationDto(newCompilationDto, currentEvents);
        compilationRepository.save(newCompilation);
        CompilationDto compilationDto = CompilationMapper.convertToCompilationDtoFromCompilation(newCompilation);
        log.info("Добавлена новая подборка {}", compilationDto);
        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
        log.info("Подборка с id = {} удалена", compId);
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        Compilation currentCompilation = findById(compId);
        CompilationDto compilationDto = CompilationMapper.convertToCompilationDtoFromCompilation(currentCompilation);
        log.info("Выполнен поиск подборки: {}", compilationDto);
        return compilationDto;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable page) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, page);
        } else {
            compilations = compilationRepository.findAll(page).toList();
        }
        List<CompilationDto> converted = CompilationMapper.convertToCompilationDtoList(compilations);
        log.info("Выполнен поиск подборок: {}", converted);
        return converted;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation currentCompilation = findById(compId);
        Set<Event> eventsForCompilation = null;
        if (updateCompilationRequest.getEvents() != null) {
            eventsForCompilation = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
        }
        CompilationMapper.convertToCompilationFromUpdateDto(currentCompilation, updateCompilationRequest, eventsForCompilation);
        compilationRepository.save(currentCompilation);
        return CompilationMapper.convertToCompilationDtoFromCompilation(currentCompilation);
    }

    private Compilation findById(Long compId) {
        Compilation currentCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id = " + compId + " не найдено"));
        return currentCompilation;
    }
}