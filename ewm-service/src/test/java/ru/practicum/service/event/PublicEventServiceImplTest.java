package ru.practicum.service.event;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.SortType;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.stats.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.utils.Constants.EVENT_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;

@ExtendWith(MockitoExtension.class)
class PublicEventServiceImplTest {
    @Mock
    private EventRepository repository;
    @Mock
    private StatsService statsService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @InjectMocks
    private EventServiceImpl service;

    private final long eventId = 1L;
    private final int from = 0;
    private final int size = 10;

    private List<Event> eventList;
    private List<Long> catIdList;

    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private List<Long> ids;
    private Map<String, Long> mapViewStats;


    @BeforeEach
    void setUp() {
        final List<Category> categories = getCategoryList();
        final List<User> users = getUserList();
        eventList = getEventList(categories, users);
        catIdList = categories.stream().map(Category::getId).collect(Collectors.toList());

        mapViewStats = Map.of(
                "/events/1", 1L,
                "/events/2", 5L,
                "/events/4", 2L,
                "/events/6", 2L);

        rangeStart = LocalDateTime.MIN;
        rangeEnd = LocalDateTime.MAX;
        ids = List.of(1L, 2L, 4L, 6L);
    }

    @Test
    void getPublishedEvent() {
        final Event publishedEvents = eventList.get(0).toBuilder().state(EventState.PUBLISHED).build();

        when(httpServletRequest.getRequestURI()).thenReturn("/events" + publishedEvents.getId());
        when(repository.findByIdAndState(anyLong(), any(EventState.class))).thenReturn(Optional.of(publishedEvents));
        doNothing().when(statsService).save(httpServletRequest);
        when(statsService.getMap(any(HttpServletRequest.class), anyBoolean())).thenReturn(mapViewStats);

        final EventFullDto actualEvent = service.getPublishedEvent(eventId, httpServletRequest);
        assertEquals(EventMapper.toFullDto(publishedEvents, 0), actualEvent);

        verify(repository, times(1)).findByIdAndState(eventId, EventState.PUBLISHED);
        verify(statsService, times(1)).getMap(httpServletRequest, true);
    }

    @Test
    void getPublishedEvent_throwException_whenNotExistRequest() {
        when(repository.findByIdAndState(anyLong(), any(EventState.class))).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getPublishedEvent(eventId, httpServletRequest));
        assertEquals(String.format(EVENT_WITH_ID_D_WAS_NOT_FOUND, eventId), exception.getMessage());

        verify(repository, times(1)).findByIdAndState(eventId, EventState.PUBLISHED);
        verify(statsService, never()).getMap(httpServletRequest, List.of(), true);
    }

    @ParameterizedTest
    @CsvSource({
            "text search,EVENT_DATE,true",
            "'',VIEWS     ,false",
            "  ,VIEWS     ,",
            "  ,     ,"
    })
    void getPublishedEvents_withSort(String text, String sort, Boolean onlyAvailable) {
        final List<Event> publishedEventList = eventList.stream()
                .filter(f -> f.getState().equals(EventState.PUBLISHED))
                .collect(Collectors.toList());
        final List<EventShortDto> collect = publishedEventList.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
        collect.get(0).setViews(1L);
        collect.get(1).setViews(5L);
        collect.get(2).setViews(2L);
        collect.get(3).setViews(2L);
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) collect.sort(Comparator.comparing(EventShortDto::getEventDate));
            if (sort.equals("VIEWS")) collect.sort(Comparator.comparing(EventShortDto::getViews));
        }

        when(httpServletRequest.getRequestURI()).thenReturn("/events");
        when(repository.findAll(any(Predicate.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(publishedEventList));
        doNothing().when(statsService).save(any(HttpServletRequest.class));
        when(statsService.getMap(any(HttpServletRequest.class), anyList(), any(), any(), anyBoolean()))
                .thenReturn(mapViewStats);

        final List<EventShortDto> alist = service.getPublishedEvents(
                text, catIdList, true, rangeStart, rangeEnd, onlyAvailable, SortType.from(sort),
                from, size, httpServletRequest);

        assertEquals(collect, alist);
    }

    @Test
    void getPublishedEvents_EmptyQueryParams() {
        final List<Event> publishedEventList = eventList.stream()
                .filter(f -> f.getState().equals(EventState.PUBLISHED))
                .collect(Collectors.toList());

        final List<EventShortDto> collect = publishedEventList.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());

        collect.get(0).setViews(1L);
        collect.get(1).setViews(5L);
        collect.get(2).setViews(2L);
        collect.get(3).setViews(2L);

        when(httpServletRequest.getRequestURI()).thenReturn("/events");
        when(repository.findAll(any(Predicate.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(publishedEventList));
        doNothing().when(statsService).save(any());
        when(statsService.getMap(any(HttpServletRequest.class), any(), any(), any(), anyBoolean()))
                .thenReturn(mapViewStats);


        final List<EventShortDto> alist = service.getPublishedEvents(null, null, null,
                null, null, false, null, from, size, httpServletRequest);
        assertEquals(collect, alist);
    }

    @Test
    void getPublishedEvents_whenReturnEmptyList() {
        when(repository.findAll(any(Predicate.class), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        final List<EventShortDto> alist = service.getPublishedEvents(
                null, null, null, null,
                null, false, null, from, size, httpServletRequest);

        assertEquals(Collections.emptyList(), alist);

        verify(statsService, times(1)).save(httpServletRequest);
        verify(statsService, never()).getMap(httpServletRequest, ids, false);
    }
}