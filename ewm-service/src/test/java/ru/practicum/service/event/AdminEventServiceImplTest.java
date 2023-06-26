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
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.location.LocationService;
import ru.practicum.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.Constants.FORMATTER;
import static ru.practicum.utils.Constants.CATEGORY_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.Constants.EVENT_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.Constants.YOU_CANNOT_S_EVENT_WHEN_CURRENT_STATUS_S;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;
import static ru.practicum.utils.TestInitDataUtil.makeCategory;
import static ru.practicum.utils.TestInitDataUtil.makeNewEventWithCorrectData;
import static ru.practicum.utils.TestInitDataUtil.makeUser;

@ExtendWith(MockitoExtension.class)
class AdminEventServiceImplTest {
    @Mock
    private EventRepository repository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private LocationService locationService;
    @InjectMocks
    private EventServiceImpl adminService;

    private final long eventId = 1L;
    private final int from = 0;
    private final int size = 10;

    private List<Event> eventList;
    private List<EventFullDto> eventFullDtos;
    private List<Long> catIdList;
    private List<Long> userIdList;
    private Event eventPending;
    private Event eventPublished;

    @BeforeEach
    void setUp() {
        final LocalDateTime createdOn = LocalDateTime.now();
        final User user = makeUser(2L, "Jon", "jon@mail.com");
        final Category category = makeCategory(2L, "Category");
        final Location location = new Location(1L, 37.62f, 55.754167f);
        final List<Category> categories = getCategoryList();
        catIdList = categories.stream().map(Category::getId).collect(Collectors.toList());
        final List<User> users = getUserList();
        userIdList = users.stream().map(User::getId).collect(Collectors.toList());

        eventList = getEventList(categories, users);
        eventFullDtos = eventList.stream().map(EventMapper::toFullDto).collect(Collectors.toList());

        final NewEventDto newEventDto = makeNewEventWithCorrectData();

        eventPending = EventMapper.fromDto(newEventDto, user, category, location, EventState.PENDING, createdOn);
        eventPublished = eventPending.toBuilder()
                .state(EventState.PUBLISHED)
                .publishedOn(LocalDateTime.now().plusMonths(2).minusHours(1))
                .build();
    }

    @ParameterizedTest
    @CsvSource({
            "2022-05-06 12:15:06, 2024-05-06 12:15:06",
            "                   , 2024-05-06 12:15:06",
            "2022-05-06 12:15:06, ",
    })
    void getEventsByAdmin(String startStr, String endStr) {
        final LocalDateTime rangeStart = (startStr != null) ? LocalDateTime.parse(startStr, FORMATTER) : null;
        final LocalDateTime rangeEnd = (endStr != null) ? LocalDateTime.parse(endStr, FORMATTER) : null;
        final List<String> states = List.of(EventState.PENDING.name(), EventState.PUBLISHED.name());

        when(repository.findAll(any(Predicate.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(eventList));

        final List<EventFullDto> actualList = adminService.getEventsByAdmin(userIdList, states, catIdList, rangeStart, rangeEnd, from, size);

        assertEquals(eventFullDtos, actualList);
    }

    @Test
    void getEventsByAdmin_withoutQuery() {
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(eventList));

        final List<EventFullDto> actualList = adminService
                .getEventsByAdmin(null, null, null, null, null, from, size);

        assertEquals(eventFullDtos, actualList);
    }

    @Test
    void getEvents2_WrongDates() {
        final LocalDateTime rangeStart = LocalDateTime.MAX;
        final LocalDateTime rangeEnd = LocalDateTime.MIN;
        final List<String> states = List.of(EventState.PENDING.name(), EventState.PUBLISHED.name());

        final ValidateException actualException = assertThrows(ValidateException.class,
                () -> adminService.getEventsByAdmin(userIdList, states, catIdList, rangeStart, rangeEnd, from, size));

        assertEquals("'rangeStart' must be before 'rangeEnd'", actualException.getMessage());
    }

    @Test
    void getEvents2_WrongStates() {
        final LocalDateTime rangeStart = LocalDateTime.MIN;
        final LocalDateTime rangeEnd = LocalDateTime.MAX;
        final List<String> states = List.of(EventState.PENDING.name(), "EventState");

        final IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class,
                () -> adminService.getEventsByAdmin(userIdList, states, catIdList, rangeStart, rangeEnd, from, size));

        assertEquals("Unknown event state: EventState", actualException.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "           ;                ;                 ;         ;       ;                 ;          ;              ;                     ",
            " new Title ; new Annotation ; new Description ; false   ; 15    ; true            ; 1        ; 15.5,65.3589 ; 2024-10-05 15:10:05 ",
            "           ;                ;                 ;         ;       ;                 ; 1        ; 15.5,65.3589 ;                     ",
            " new Title ;                ;                 ;         ;       ;                 ;          ; 15.5,65.3589 ;                     "
    }, delimiter = ';')
    void updateEventByAdmin(String title, String annotation, String description,
                            Boolean isPaid, Integer limit, Boolean isModeration,
                            Long categoryId, String location, String eventDate) {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setTitle(title);
        newBody.setAnnotation(annotation);
        newBody.setDescription(description);
        newBody.setPaid(isPaid);
        newBody.setParticipantLimit(limit);
        if (eventDate != null) {
            newBody.setEventDate(LocalDateTime.parse(eventDate, FORMATTER));
        }
        newBody.setRequestModeration(isModeration);
        newBody.setCategory(categoryId);
        LocationDto locationDto = null;
        if (location != null) {
            String[] split = location.split(",");
            locationDto = new LocationDto(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
            newBody.setLocation(locationDto);
        }
        final Event updatedEvent = getUpdatedEvent(newBody, eventPending);
        final EventFullDto updatedFullDto = EventMapper.toFullDto(updatedEvent);

        when(repository.findById(anyLong())).thenReturn(Optional.of(eventPending));
        when(repository.save(any())).thenReturn(updatedEvent);
        if (categoryId != null) {
            final Category cat = new Category(categoryId, "new Category");
            when(categoryService.findCategoryById(anyLong())).thenReturn(cat);
        }
        if (location != null) {
            final Location currentLocation = LocationMapper.fromDto(locationDto);
            when(locationService.findLocation(any())).thenReturn(currentLocation);
        }

        final EventFullDto actualEvent = adminService.updateEventByAdmin(newBody, eventId);
        assertEquals(updatedFullDto, actualEvent);
    }

    @ParameterizedTest
    @CsvSource({
            "PUBLISH_EVENT, PENDING,  2",
            "REJECT_EVENT,  PENDING,  2",
    })
    void updateEventByAdmin_setPUBLISHED_whenCorrectStat(String newStatus, String eventState, int eventIndex) {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setStateAction(newStatus);
        final EventStateAction stateAction = EventStateAction.from(newStatus);
        final EventState newEventState = stateAction.getEventState();
        final EventState currentState = EventState.from(eventState);

        final Event event = eventList.get(eventIndex);
        final Event updatedEvent = getUpdatedEvent(newBody, event);

        final EventFullDto updatedFullDto = EventMapper.toFullDto(updatedEvent);
        updatedFullDto.setState(newEventState);
        updatedFullDto.setPublishedOn(LocalDateTime.now());

        when(repository.findById(anyLong())).thenReturn(Optional.of(event));
        when(repository.save(any())).thenReturn(updatedEvent);

        assertEquals(currentState, event.getState());
        assertNull(this.eventPending.getPublishedOn());

        final EventFullDto eventFullDto = adminService.updateEventByAdmin(newBody, eventId);
        assertEquals(newEventState, eventFullDto.getState());
        assertNotNull(eventFullDto.getPublishedOn());

        verify(repository, times(1)).findById(eventId);
        verify(repository, times(1)).save(updatedEvent);
    }


    @Test
    void updateEventByAdmin_setPUBLISHED_whenEventPUBLISHED() {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setStateAction("PUBLISH_EVENT");
        final Event publishedEvent = eventList.get(0);

        when(repository.findById(anyLong())).thenReturn(Optional.of(publishedEvent));

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> adminService.updateEventByAdmin(newBody, eventId));
        assertEquals("You cannot PUBLISHED event when current status PUBLISHED", exception.getMessage());

        verify(repository, times(1)).findById(eventId);
        verify(repository, never()).save(new Event());
    }

    @Test
    void updateEventByAdmin_setREJECT_whenEventPUBLISHED() {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setStateAction("REJECT_EVENT");
        final Event publishedEvent = eventList.get(0);

        when(repository.findById(anyLong())).thenReturn(Optional.of(publishedEvent));

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> adminService.updateEventByAdmin(newBody, eventId));
        assertEquals(String.format(YOU_CANNOT_S_EVENT_WHEN_CURRENT_STATUS_S, EventState.CANCELED, EventState.PUBLISHED),
                exception.getMessage());

        verify(repository, times(1)).findById(eventId);
        verify(repository, never()).save(new Event());
    }

    @Test
    void updateEventByAdmin_setREJECT_whenEventCANCELED() {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setStateAction("REJECT_EVENT");
        final Event canceledEvent = eventList.get(4);

        when(repository.findById(anyLong())).thenReturn(Optional.of(canceledEvent));

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> adminService.updateEventByAdmin(newBody, eventId));
        assertEquals(
                String.format(YOU_CANNOT_S_EVENT_WHEN_CURRENT_STATUS_S, EventState.CANCELED, EventState.CANCELED),
                exception.getMessage());

        verify(repository, times(1)).findById(eventId);
        verify(repository, never()).save(new Event());
    }


    @Test
    void updateEventByAdmin_throwException_whenSetPUBLISHED_whenEventCANCELD() {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setStateAction("PUBLISH_EVENT");

        final Event canceledEvent = eventList.get(4);

        when(repository.findById(anyLong())).thenReturn(Optional.of(canceledEvent));

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> adminService.updateEventByAdmin(newBody, eventId));
        assertEquals(String.format(YOU_CANNOT_S_EVENT_WHEN_CURRENT_STATUS_S, EventState.PUBLISHED, EventState.CANCELED),
                exception.getMessage());

        verify(repository, times(1)).findById(eventId);
        verify(repository, never()).save(new Event());
    }

    @Test
    void updateEventByAdmin_throwException_whenUnknownState() {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setStateAction("WRONG_STATE");

        final Event updatedEvent = getUpdatedEvent(newBody, eventPending);

        when(repository.findById(anyLong())).thenReturn(Optional.of(eventPending));

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminService.updateEventByAdmin(newBody, eventId));

        assertEquals("Unknown event state action: WRONG_STATE", exception.getMessage());

        verify(repository, times(1)).findById(eventId);
        verify(repository, never()).save(updatedEvent);
    }

    @ParameterizedTest
    @CsvSource({
            "2022-08-05 15:02:12, Event date and time cannot be earlier than 2 hours from the current moment.",
            "                   , Event date and time cannot be earlier than 2 hours from the published moment."
    })
    void updateEventByAdmin_throwException_withWrongDate(String newEventDate, String expectMsg) {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setEventDate((newEventDate != null)
                ? LocalDateTime.parse(newEventDate, FORMATTER)
                : LocalDateTime.now().plusMonths(2));


        final Event currentEvent = (newEventDate != null) ? eventPending : eventPublished;
        final Event updatedEvent = getUpdatedEvent(newBody, currentEvent);

        when(repository.findById(anyLong())).thenReturn(Optional.of(currentEvent));

        final ValidateException exception = assertThrows(ValidateException.class,
                () -> adminService.updateEventByAdmin(newBody, eventId));

        assertEquals(expectMsg, exception.getMessage());

        verify(repository, times(1)).findById(eventId);
        verify(repository, never()).save(updatedEvent);
    }

    @Test
    void updateEventByAdmin_setPublished_whenCorrectDate() {
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setEventDate(LocalDateTime.now().plusMonths(3));

        final Event updatedEvent = getUpdatedEvent(newBody, eventPublished);
        final EventFullDto fullDto = EventMapper.toFullDto(updatedEvent);

        when(repository.findById(anyLong())).thenReturn(Optional.of(eventPublished));
        when(repository.save(any())).thenReturn(updatedEvent);

        final EventFullDto actualEvent = adminService.updateEventByAdmin(newBody, eventId);
        assertEquals(fullDto, actualEvent);
    }

    @Test
    void updateEvent1_throwException_whenNoExistEvent() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> adminService.updateEventByAdmin(new UpdateEventAdminRequest(), eventId));
        assertEquals(String.format(EVENT_WITH_ID_D_WAS_NOT_FOUND, eventId), actualException.getMessage());
    }

    @Test
    void updateEventByAdmin_throwException_WrongCategory() {
        final long catId = 6L;
        final UpdateEventAdminRequest newBody = new UpdateEventAdminRequest();
        newBody.setCategory(catId);

        when(repository.findById(anyLong())).thenReturn(Optional.of(eventPending));
        doThrow(new NotFoundException(String.format(CATEGORY_WITH_ID_D_WAS_NOT_FOUND, newBody.getCategory())))
                .when(categoryService).findCategoryById(catId);

        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> adminService.updateEventByAdmin(newBody, eventId));

        assertEquals(String.format(Constants.CATEGORY_WITH_ID_D_WAS_NOT_FOUND, catId), actualException.getMessage());
    }

    private Event getUpdatedEvent(UpdateEventAdminRequest body, Event event) {
        if (body.getTitle() != null) {
            event.setTitle(body.getTitle());
        }
        if (body.getAnnotation() != null) {
            event.setAnnotation(body.getAnnotation());
        }
        if (body.getDescription() != null) {
            event.setDescription(body.getDescription());
        }
        if (body.getParticipantLimit() != null) {
            event.setParticipantLimit(body.getParticipantLimit());
        }
        if (body.getPaid() != null) {
            event.setPaid(body.getPaid());
        }
        if (body.getRequestModeration() != null) {
            event.setRequestModeration(body.getRequestModeration());
        }
        if (body.getEventDate() != null) {
            event.setEventDate(body.getEventDate());
        }
        if (body.getCategory() != null) {
            Category newCategory = new Category(body.getCategory(), "new Category");
            event.setCategory(newCategory);
        }
        if (body.getLocation() != null) {
            event.setLocation(new Location(5L, body.getLocation().getLat(), body.getLocation().getLon()));
        }
        return event;
    }
}