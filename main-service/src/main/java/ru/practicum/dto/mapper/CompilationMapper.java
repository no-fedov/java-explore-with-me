package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation convertFromNewCompilationDto(NewCompilationDto newCompilationDto, Set<Event> eventList) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .events(eventList)
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public static CompilationDto convertToCompilationDtoFromCompilation(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents() == null ? null : EventMapper.convertToSetEventDto(compilation.getEvents()))
                .build();
    }

    public static List<CompilationDto> convertToCompilationDtoList(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::convertToCompilationDtoFromCompilation).toList();
    }

    public static Compilation convertToCompilationFromUpdateDto(Compilation compilation,
                                                                UpdateCompilationRequest updateCompilationRequest,
                                                                Set<Event> events) {
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        compilation.setEvents(events);
        return compilation;
    }
}