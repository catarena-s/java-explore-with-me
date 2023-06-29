package ru.practicum.service.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.utils.Constants;
import ru.practicum.utils.TestInitDataUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicCompilationServiceTest {
    @Mock
    private CompilationRepository repository;
    @InjectMocks
    private CompilationServiceImpl service;
    private final long compId = 1L;
    private List<Event> eventList;
    private List<Long> eventIds;
    private NewCompilationDto body;
    private Compilation compilation;
    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        eventList = TestInitDataUtil.getEventList(TestInitDataUtil.getCategoryList(), TestInitDataUtil.getUserList());
        eventIds = eventList.stream().map(Event::getId).collect(Collectors.toList());

        body = NewCompilationDto.builder().title("Compilation").pinned(true).events(eventIds).build();

        compilation = CompilationMapper.fromDto(body, eventList);
    }

    @Test
    void getCompilation() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(compilation));

        final CompilationDto actualCompilation = service.getCompilation(compId);
        assertEquals(CompilationMapper.toDto(compilation), actualCompilation);
    }

    @Test
    void getCompilation_NotExpected() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getCompilation(compId));

        assertEquals(String.format(Constants.COMPILATION_WITH_ID_WAS_NOT_FOUND, compId), exception.getMessage());
    }

    @Test
    void getCompilations() {
        final List<Compilation> compilations = List.of(compilation);
        final List<CompilationDto> expectedList = compilations.stream()
                .map(CompilationMapper::toDto).collect(Collectors.toList());
        when(repository.findAllByPinned(anyBoolean(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(compilations));

        final List<CompilationDto> actualList = service.getCompilations(true, from, size);
        assertEquals(expectedList, actualList);
    }

    @Test
    void getCompilations_whenNullPinned() {
        final List<Compilation> compilations = List.of(compilation);
        final List<CompilationDto> expectedList = compilations.stream()
                .map(CompilationMapper::toDto).collect(Collectors.toList());
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(compilations));

        final List<CompilationDto> actualList = service.getCompilations(null, from, size);
        assertEquals(expectedList, actualList);
    }
}