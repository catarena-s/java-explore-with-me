package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation fromDto(NewCompilationDto body, List<Event> events) {
        return Compilation.builder()
                .title(body.getTitle())
                .pinned(body.isPinned())
                .events(events)
                .build();
    }

    public static CompilationDto toDto(Compilation c) {
        return CompilationDto.builder()
                .id(c.getId())
                .title(c.getTitle())
                .pinned(c.getPinned())
                .events(c.getEvents() == null ? Collections.emptyList() :
                        c.getEvents().stream().map(EventMapper::toShortDto).collect(Collectors.toList()))
                .build();
    }
}
