package ru.practicum.service.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {
    @Mock
    private CompilationRepository repository;
    @InjectMocks
    private CompilationServiceImpl service;
    private final long compId = 1L;
    private List<Event> eventList;
    private List<Long> eventIds;
    private NewCompilationDto body;
    private Compilation compilation;

    @BeforeEach
    void setUp() {
        eventList = TestInitDataUtil.getEventList(TestInitDataUtil.getCategoryList(), TestInitDataUtil.getUserList());
        eventIds = eventList.stream().map(Event::getId).collect(Collectors.toList());

        body = NewCompilationDto.builder().title("Compilation").pinned(true).events(eventIds).build();

        compilation = CompilationMapper.fromDto(body, eventList);
    }

    @Test
    void findCompilationById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(compilation));

        final Compilation actualCompilation = service.findCompilationById(compId);
        assertEquals(compilation, actualCompilation);

        verify(repository, times(1)).findById(compId);
    }

    @Test
    void findCategoryById_whenNotExist() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.findCompilationById(compId));
        assertEquals(String.format(Constants.COMPILATION_WITH_ID_WAS_NOT_FOUND, compId), exception.getMessage());

        verify(repository, times(1)).findById(compId);
    }
}