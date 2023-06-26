package ru.practicum.service.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationService {
    void delete(long compId);

    CompilationDto saveCompilation(NewCompilationDto body);

    CompilationDto updateCompilation(UpdateCompilationRequest body, long compId);

    Compilation findCompilationById(long compId);

    CompilationDto getCompilation(long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
