package ru.practicum.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.utils.Constants.EVENT_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.Constants.THE_REQUIRED_OBJECT_WAS_NOT_FOUND;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository repository;
    @InjectMocks
    private EventServiceImpl service;

    @Test
    void findEventById() {
        final List<Event> eventList = getEventList(getCategoryList(), getUserList());
        final Event event = eventList.get(0);
        final long eventId = event.getId();
        when(repository.findById(anyLong())).thenReturn(Optional.of(event));

        Event actualEvent = service.findEventById(eventId);
        assertEquals(event, actualEvent);
    }

    @Test
    void findEventById_throwException_whenEventNotExist() {
        final long eventId = 1L;
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.findEventById(eventId));
        ApiError apiError = exception.getApiError();
        assertEquals(String.format(EVENT_WITH_ID_D_WAS_NOT_FOUND, eventId), apiError.getMessage());
        assertEquals(THE_REQUIRED_OBJECT_WAS_NOT_FOUND, apiError.getReason());
    }

    @Test
    void findEventsByIds() {
        final List<Event> eventList = getEventList();
        when(repository.findAllById(any())).thenReturn(eventList);

        final List<Event> events = service.findEventsByIds(List.of(1L, 2L, 3L));
        assertEquals(eventList, events);
    }
}