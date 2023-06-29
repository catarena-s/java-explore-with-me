package ru.practicum.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.location.LocationService;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.Constants.FORMATTER;
import static ru.practicum.utils.Constants.CATEGORY_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.Constants.USER_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;
import static ru.practicum.utils.TestInitDataUtil.makeCategory;
import static ru.practicum.utils.TestInitDataUtil.makeNewEventWithCorrectData;
import static ru.practicum.utils.TestInitDataUtil.makeUser;

@ExtendWith(MockitoExtension.class)
class PrivetEventServiceImplTest {
    @Mock
    private EventRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private LocationService locationService;
    @InjectMocks
    private EventServiceImpl service;

    private final long userId = 2L;
    private final long eventId = 1L;
    private final int from = 0;
    private final int size = 10;

    private List<Event> eventList;
    private User user;
    private Category category;
    private Location location;
    private LocationDto locationDto;
    private NewEventDto newEventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        user = makeUser(2L, "Jon", "jon@mail.com");
        category = makeCategory(2L, "Category");
        location = new Location(1L, 37.62f, 55.754167f);
        locationDto = new LocationDto(37.62f, 55.754167f);
        eventList = getEventList(getCategoryList(), getUserList());

        newEventDto = makeNewEventWithCorrectData();
        event = EventMapper.fromDto(newEventDto, user, category, location, EventState.PENDING, LocalDateTime.now());
    }

    @Test
    void getEvents() {
        doNothing().when(userService).checkExistById(anyLong());
        when(repository.findAllByInitiatorId(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(eventList));

        final List<EventShortDto> actualList = service.getEvents(userId, from, size);
        final List<EventShortDto> collect = eventList.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
        assertEquals(collect, actualList);
    }

    @Test
    void getEvents_NotFoundUser() {
        doThrow(new NotFoundException(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId)))
                .when(userService).checkExistById(anyLong());

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> service.getEvents(userId, from, size));

        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId), actualException.getMessage());
    }

    @Test
    void addEvent() {
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(categoryService.findCategoryById(anyLong())).thenReturn(category);
        when(locationService.findLocation(any())).thenReturn(location);
        when(repository.save(any())).thenReturn(event);

        final EventFullDto actualEventDto = service.saveEvent(userId, newEventDto);

        assertEquals(EventMapper.toFullDto(event), actualEventDto);

        verify(userService, times(1)).findUserById(userId);
        verify(categoryService, times(1)).findCategoryById(2L);
        verify(locationService, times(1)).findLocation(locationDto);
    }

    @Test
    void addEvent_withWrongDates() {
        final NewEventDto wrongNewEventDto = newEventDto.toBuilder()
                .eventDate(LocalDateTime.parse("2022-12-31 15:10:05", FORMATTER))
                .build();

        final ValidateException exception = assertThrows(ValidateException.class,
                () -> service.saveEvent(userId, wrongNewEventDto));

        assertEquals("Event date and time cannot be earlier than 2 hours from the current moment.",
                exception.getMessage());
        verify(userService, never()).findUserById(userId);
        verify(categoryService, never()).findCategoryById(2L);
        verify(locationService, never()).findLocation(locationDto);
        verify(repository, never()).save(new Event());
    }

    @Test
    void addEvent_NotFoundUser() {
        when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId)));

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> service.saveEvent(userId, newEventDto));

        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId), actualException.getMessage());

        verify(userService, times(1)).findUserById(userId);
        verify(categoryService, never()).findCategoryById(2L);
        verify(locationService, never()).findLocation(locationDto);
        verify(repository, never()).save(event);
    }

    @Test
    void addEvent_NotFoundCategory() {
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(categoryService.findCategoryById(anyLong())).thenThrow(
                new NotFoundException(String.format(CATEGORY_WITH_ID_D_WAS_NOT_FOUND, newEventDto.getCategory()))
        );

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> service.saveEvent(userId, newEventDto));

        assertEquals(String.format(CATEGORY_WITH_ID_D_WAS_NOT_FOUND, newEventDto.getCategory()),
                actualException.getMessage());

        verify(userService, times(1)).findUserById(userId);
        verify(categoryService, times(1)).findCategoryById(2L);

        verify(locationService, never()).findLocation(locationDto);
        verify(repository, never()).save(event);
    }

    @Test
    void addEvent_NotFoundLocation() {
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(categoryService.findCategoryById(anyLong())).thenReturn(category);
        when(locationService.findLocation(any())).thenReturn(location);
        when(repository.save(any())).thenReturn(event);

        final EventFullDto actualEventDto = service.saveEvent(userId, newEventDto);

        assertEquals(EventMapper.toFullDto(event), actualEventDto);

        verify(userService, times(1)).findUserById(userId);
        verify(categoryService, times(1)).findCategoryById(2L);
        verify(locationService, times(1)).findLocation(locationDto);
        verify(repository, never()).save(new Event());
    }

    @Test
    void getEvent() {
        doNothing().when(userService).checkExistById(anyLong());
        when(repository.findByIdAndInitiatorId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(event));

        final EventFullDto foundedEvent = service.getEvent(userId, eventId);

        assertEquals(EventMapper.toFullDto(event), foundedEvent);

        verify(userService, times(1)).checkExistById(userId);
        verify(repository, times(1)).findByIdAndInitiatorId(eventId, userId);
    }

    @Test
    void getEvent_NotFoundUser() {
        doThrow(new NotFoundException(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId)))
                .when(userService).checkExistById(anyLong());

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> service.getEvent(userId, eventId));

        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId), actualException.getMessage());
    }

    @Test
    void getEvent_NotFoundEvent() {
        doNothing().when(userService).checkExistById(anyLong());
        when(repository.findByIdAndInitiatorId(anyLong(), anyLong())).thenReturn(Optional.empty());

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> service.getEvent(userId, eventId));
    }

    @Test
    void updateEvent() {
        final UpdateEventUserRequest newBody = new UpdateEventUserRequest();
        newBody.setAnnotation("new Annotation");
        newBody.setDescription("new Description");
        newBody.setPaid(false);
        newBody.setParticipantLimit(15);
        newBody.setEventDate(LocalDateTime.parse("2024-10-05 15:10:05", FORMATTER));

        final Event updatedEvent = event.toBuilder()
                .annotation(newBody.getAnnotation())
                .description(newBody.getDescription())
                .eventDate(newBody.getEventDate())
                .paid(newBody.getPaid())
                .participantLimit(newBody.getParticipantLimit())
                .build();

        final EventFullDto updatedFullDto = EventMapper.toFullDto(updatedEvent);

        doNothing().when(userService).checkExistById(anyLong());
        when(repository.findByIdAndInitiatorId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(event));
        when(repository.save(any())).thenReturn(updatedEvent);

        final EventFullDto eventFullDto = service.updateEventByUser(newBody, userId, eventId);
        assertEquals(updatedFullDto, eventFullDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SEND_TO_REVIEW", "CANCEL_REVIEW"})
    void updateEvent_changeState_whenEventPending(String newState) {
        final UpdateEventUserRequest newBody = new UpdateEventUserRequest();
        newBody.setStateAction(EventStateAction.from(newState));

        final Event baseEvent = eventList.get(2);
        final Event updatedEvent = baseEvent.toBuilder().state(EventState.PUBLISHED).build();
        final EventFullDto updatedFullDto = EventMapper.toFullDto(updatedEvent);

        doNothing().when(userService).checkExistById(anyLong());
        when(repository.findByIdAndInitiatorId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(baseEvent));
        when(repository.save(any())).thenReturn(updatedEvent);

        final EventFullDto eventFullDto = service.updateEventByUser(newBody, userId, eventId);
        assertEquals(updatedFullDto, eventFullDto);
    }


    @ParameterizedTest
    @ValueSource(strings = {"PUBLISH_EVENT", "REJECT_EVENT"})
    void updateEvent_throwException_whenWrongState(String newState) {
        final UpdateEventUserRequest newBody = new UpdateEventUserRequest();
        newBody.setStateAction(EventStateAction.from(newState));

        final Event baseEvent = eventList.get(2);

        doNothing().when(userService).checkExistById(anyLong());
        when(repository.findByIdAndInitiatorId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(baseEvent));

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.updateEventByUser(newBody, userId, eventId));
        assertEquals("Wrong status. Status should be one of: [SEND_TO_REVIEW, CANCEL_REVIEW]", exception.getMessage());
    }

    @Test
    void updateEvent_throwException_whenEventPUBLISHED() {
        final Event baseEvent = eventList.get(0);
        final UpdateEventUserRequest newBody = new UpdateEventUserRequest();
        newBody.setStateAction(EventStateAction.from("PUBLISH_EVENT"));

        doNothing().when(userService).checkExistById(anyLong());
        when(repository.findByIdAndInitiatorId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(baseEvent));

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.updateEventByUser(newBody, userId, eventId));
        assertEquals("Event state is Published", exception.getMessage());

        verify(repository, never()).save(new Event());
    }

    @Test
    void updateEvent_throwException_whenNoExistUser() {
        doThrow(new NotFoundException(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId)))
                .when(userService).checkExistById(anyLong());


        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> service.updateEventByUser(new UpdateEventUserRequest(), userId, eventId));

        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId), actualException.getMessage());
    }
}