package ru.practicum.service.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.service.event.EventService;
import ru.practicum.utils.Constants;
import ru.practicum.utils.TestInitDataUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCompilationServiceTest {
    @Mock
    private CompilationRepository repository;
    @Mock
    private EventService eventService;
    @InjectMocks
    private CompilationServiceImpl compilationService;

    private final Long compId = 1L;
    private NewCompilationDto body;
    private NewCompilationDto body2;
    private CompilationDto compilationDto;
    private CompilationDto compilationDto2;
    private List<Event> eventList;
    private List<Long> eventIds;
    private Compilation compilation;
    private Compilation compilation2;
    private UpdateCompilationRequest updateCompilation;
    private EventShortDto eventShortDto;
    private UpdateCompilationRequest updateCompilationWithEvents;
    private UpdateCompilationRequest updateCompilationWithEmptyEvents;

    @BeforeEach
    void setUp() {
        eventList = TestInitDataUtil.getEventList(TestInitDataUtil.getCategoryList(), TestInitDataUtil.getUserList());
        eventShortDto = EventMapper.toShortDto(eventList.get(0));
        eventIds = eventList.stream().map(Event::getId).collect(Collectors.toList());

        body = NewCompilationDto.builder().title("Compilation").pinned(true).events(eventIds).build();
        body2 = NewCompilationDto.builder().title("Compilation").pinned(true).build();

        compilation = CompilationMapper.fromDto(body, eventList);
        compilation2 = CompilationMapper.fromDto(body, Collections.emptyList());

        compilationDto = CompilationMapper.toDto(compilation);
        compilationDto2 = CompilationMapper.toDto(compilation2);

        updateCompilation = UpdateCompilationRequest.builder()
                .title("NewTitle")
                .pinned(false)
                .build();
        updateCompilationWithEvents = updateCompilation.toBuilder()
                .events(List.of(eventShortDto.getId()))
                .build();
        updateCompilationWithEmptyEvents = updateCompilation.toBuilder()
                .events(Collections.emptyList())
                .build();
    }

    @Test
    void saveCompilation() {
        when(eventService.findEventsByIds(any())).thenReturn(eventList);
        when(repository.save(any())).thenReturn(compilation);

        final CompilationDto actualCompilationDto = compilationService.saveCompilation(body);
        assertEquals(compilationDto, actualCompilationDto);

        verify(eventService, times(1)).findEventsByIds(eventIds);
        verify(repository, times(1)).save(compilation);
    }

    @Test
    void saveCompilation_emptyEventList() {
        when(repository.save(any())).thenReturn(compilation2);

        final CompilationDto actualCompilationDto = compilationService.saveCompilation(body2);
        assertEquals(compilationDto2, actualCompilationDto);

        verify(eventService, never()).findEventsByIds(null);
        verify(repository, times(1)).save(compilation2);
    }

    @Test
    @DisplayName("Compilation with  same title already exists")
    void saveCompilation_throwException() {
        when(eventService.findEventsByIds(any())).thenReturn(eventList);
        when(repository.save(any())).thenThrow(DataIntegrityViolationException.class);

        final ConflictException exception = assertThrows(ConflictException.class, () -> compilationService.saveCompilation(body));
        assertEquals(String.format("Compilation with title='%s' already exists", body.getTitle()), exception.getMessage());

        verify(eventService, times(1)).findEventsByIds(eventIds);
        verify(repository, times(1)).save(compilation);
    }

    @ParameterizedTest
    @CsvSource({
            ",",
            "new CompilationTitle, true"
    })
    void updateCompilation(String title, Boolean pinned) {
        when(repository.findById(anyLong())).thenReturn(Optional.of(compilation));
        when(repository.save(any())).thenReturn(compilation);

        updateCompilation.setTitle(title);
        updateCompilation.setPinned(pinned);

        final CompilationDto exitedCompilation = compilationDto.toBuilder()
                .title(title != null ? updateCompilation.getTitle() : compilationDto.getTitle())
                .pinned(pinned != null ? updateCompilation.getPinned() : compilationDto.getPinned())
                .build();
        final CompilationDto actualCompilationDto = compilationService.updateCompilation(updateCompilation, compId);
        assertEquals(exitedCompilation, actualCompilationDto);

        verify(repository, times(1)).findById(compId);
        verify(eventService, never()).findEventsByIds(eventIds);
        verify(repository, times(1)).save(compilation);
    }

    @Test
    void updateCompilation_withNewEvents() {
        final Compilation exitedCompilation = compilation.toBuilder()
                .title(updateCompilation.getTitle())
                .pinned(updateCompilation.getPinned())
                .events(List.of(eventList.get(0)))
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(compilation));
        when(repository.save(any())).thenReturn(exitedCompilation);


        final CompilationDto actualCompilationDto = compilationService.updateCompilation(updateCompilationWithEvents, compId);
        assertEquals(CompilationMapper.toDto(exitedCompilation), actualCompilationDto);

        verify(repository, times(1)).findById(compId);
        verify(eventService, never()).findEventsByIds(eventIds);
        verify(repository, times(1)).save(compilation);
    }

    @Test
    void updateCompilation_withEmptyEvents() {
        final Compilation exitedCompilation = compilation.toBuilder()
                .title(updateCompilation.getTitle())
                .pinned(updateCompilation.getPinned())
                .events(Collections.emptyList())
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(compilation));
        when(repository.save(any())).thenReturn(exitedCompilation);

        final CompilationDto actualCompilationDto = compilationService.updateCompilation(updateCompilationWithEmptyEvents, compId);
        assertEquals(CompilationMapper.toDto(exitedCompilation), actualCompilationDto);

        verify(repository, times(1)).findById(compId);
        verify(eventService, never()).findEventsByIds(eventIds);
        verify(repository, times(1)).save(compilation);
    }

    @Test
    void updateCompilation_NotExist() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> compilationService.updateCompilation(updateCompilation, compId));
        assertEquals(String.format(Constants.COMPILATION_WITH_ID_WAS_NOT_FOUND, compId), exception.getMessage());

        verify(repository, times(1)).findById(compId);
        verify(eventService, never()).findEventsByIds(eventIds);
        verify(repository, never()).save(compilation);
    }


    @Test
    void delete() {
        when(repository.existsById(compId)).thenReturn(true);
        doNothing().when(repository).deleteById(compId);

        compilationService.delete(compId);
        verify(repository, times(1)).existsById(compId);
        verify(repository, times(1)).deleteById(compId);
    }

    @Test
    void delete_NotExist() {
        when(repository.existsById(compId)).thenReturn(false);

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> compilationService.delete(compId));
        assertEquals(String.format(Constants.COMPILATION_WITH_ID_WAS_NOT_FOUND, compId), exception.getMessage());
        verify(repository, times(1)).existsById(compId);
        verify(repository, never()).deleteById(compId);
    }
}