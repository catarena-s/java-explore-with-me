package ru.practicum.service.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.event.EventService;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
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
import static ru.practicum.enums.RequestStatus.CANCELED;
import static ru.practicum.enums.RequestStatus.CONFIRMED;
import static ru.practicum.enums.RequestStatus.PENDING;
import static ru.practicum.enums.RequestStatus.REJECTED;
import static ru.practicum.utils.Constants.EVENT_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.Constants.USER_WITH_ID_D_WAS_NOT_FOUND;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private RequestRepository repository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    @Mock
    private EventService eventService;
    @InjectMocks
    private RequestServiceImpl service;
    private final long eventId = 1L;
    private final long userId2 = 2L;
    private Request request;
    private User user;
    private Event event;
    private List<User> userList;
    private List<Event> eventList;
    private Request request1;
    private List<Request> requestList;
    private List<ParticipationRequestDto> dtoList;
    private Event eventUser2;
    private List<ParticipationRequestDto> dtoRequestListConfirmed;
    private List<ParticipationRequestDto> dtoRequestListRejected;
    private List<Request> regectList;
    private Request request2;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("User1").email("email@mail.com").build();
        userList = getUserList();
        eventList = getEventList();
        event = eventList.get(0);
        eventUser2 = eventList.get(1);

        request1 = Request.builder()
                .id(1L).requester(user).event(event).created(LocalDateTime.now()).status(PENDING)
                .build();
        request2 = Request.builder()
                .id(2L).requester(user).event(event).created(LocalDateTime.now()).status(PENDING)
                .build();
        requestList = List.of(request1);
        regectList = List.of(request2);

        dtoList = requestList.stream().map(RequestMapper::toDto).collect(Collectors.toList());
        dtoRequestListConfirmed = requestList.stream().map(RequestMapper::toDto).collect(Collectors.toList());
        dtoRequestListConfirmed.get(0).setStatus(CONFIRMED);

        dtoRequestListRejected = regectList.stream().map(RequestMapper::toDto).collect(Collectors.toList());
        dtoRequestListRejected.get(0).setStatus(REJECTED);
    }

    private Request makeRequest() {
        return Request.builder()
                .requester(userList.get(1))
                .event(eventList.get(0))
                .status(PENDING)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addParticipationRequest() {
        request = makeRequest();
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.findEventById(anyLong())).thenReturn(event);
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(request);

        final ParticipationRequestDto actualRequest = service.addParticipationRequest(userId2, eventId);
        assertEquals(RequestMapper.toDto(request), actualRequest);

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventId, userId2);
        verify(eventService, times(1)).findEventById(eventId);
        verify(userService, times(1)).findUserById(userId2);
    }

    @Test
    void addParticipationRequest2() {
        request = Request.builder()
                .requester(userList.get(0))
                .event(eventList.get(1))
                .status(CONFIRMED)
                .created(LocalDateTime.now())
                .build();
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.findEventById(anyLong())).thenReturn(event);
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(request);

        final ParticipationRequestDto actualRequest = service.addParticipationRequest(userId2, eventId);
        assertEquals(RequestMapper.toDto(request), actualRequest);

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventId, userId2);
        verify(eventService, times(1)).findEventById(eventId);
        verify(userService, times(1)).findUserById(userId2);
    }

    @Test
    void addParticipationRequest_ParticipantLimitIs0() {
        final Event currentEvent = eventList.get(1);
        final Long eventId = currentEvent.getId();
        final User requester = userList.get(2);
        final Long requesterId = requester.getId();
        final Request newRequest = Request.builder()
                .requester(requester)
                .event(currentEvent)
                .created(LocalDateTime.now())
                .build();
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.findEventById(anyLong())).thenReturn(currentEvent);
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(newRequest);

        final ParticipationRequestDto actualRequest = service.addParticipationRequest(requesterId, eventId);
        assertEquals(RequestMapper.toDto(newRequest), actualRequest);

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventId, requesterId);
        verify(eventService, times(1)).findEventById(eventId);
        verify(userService, times(1)).findUserById(requesterId);
    }

    @Test
    void addParticipationRequest_RequestModerationIsFalse() {
        final Event currentEvent = eventList.get(3);
        final Long eventId = currentEvent.getId();
        final User requester = userList.get(2);
        final Long requesterId = requester.getId();

        final Request newRequest = Request.builder()
                .requester(requester)
                .event(currentEvent)
                .created(LocalDateTime.now())
                .build();

        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.findEventById(anyLong())).thenReturn(currentEvent);
        when(userService.findUserById(anyLong())).thenReturn(user);
        when(repository.save(any())).thenReturn(newRequest);

        final ParticipationRequestDto actualRequest = service.addParticipationRequest(requesterId, eventId);
        assertEquals(RequestMapper.toDto(newRequest), actualRequest);

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventId, requesterId);
        verify(eventService, times(1)).findEventById(eventId);
        verify(userService, times(1)).findUserById(requesterId);
    }

    @Test
    @DisplayName("addParticipationRequest - Нельзя добавить повторный запрос")
    void addParticipationRequest_SameRequest() {
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(true);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.addParticipationRequest(userId2, eventId));

        assertEquals(String.format("Request for eventId=%d from userId=%d already exist.", eventId, userId2),
                exception.getMessage());

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventId, userId2);
        verify(eventRepository, never()).findById(eventId);
        verify(userService, never()).findUserById(userId2);
        verify(repository, never()).save(request);
    }

    @Test
    @DisplayName("addParticipationRequest - Event Not Found")
    void addParticipationRequest_EventNotExist() {
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        doThrow(new NotFoundException(String.format(EVENT_WITH_ID_D_WAS_NOT_FOUND, eventId)))
                .when(eventService).findEventById(anyLong());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.addParticipationRequest(userId2, eventId));

        assertEquals(String.format(EVENT_WITH_ID_D_WAS_NOT_FOUND, eventId), exception.getMessage());

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventId, userId2);
        verify(eventService, times(1)).findEventById(eventId);
        verify(userService, never()).findUserById(userId2);
        verify(repository, never()).save(request);
    }

    @Test
    @DisplayName("addParticipationRequest - инициатор события не может добавить запрос на участие в своём событии")
    void addParticipationRequest_ByEventOwner() {
        final Long eventID = eventUser2.getId();
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.findEventById(anyLong())).thenReturn(eventUser2);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.addParticipationRequest(userId2, eventID));

        assertEquals(String.format("UserId=%d is initiator for event with id=%d", userId2, eventID), exception.getMessage());

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventID, userId2);
        verify(eventService, times(1)).findEventById(eventID);
        verify(userService, never()).findUserById(userId2);
        verify(repository, never()).save(new Request());
    }

    @Test
    @DisplayName("addParticipationRequest - нельзя участвовать в неопубликованном событии")
    void addParticipationRequest_NotPublishedEvent() {
        final Event expectedEvent = eventList.get(2);
        final Long eventID = expectedEvent.getId();

        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.findEventById(anyLong())).thenReturn(expectedEvent);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.addParticipationRequest(userId2, eventID));

        assertEquals(String.format("Event id=%d is not published", eventID), exception.getMessage());

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventID, userId2);
        verify(eventService, times(1)).findEventById(eventID);
        verify(userService, never()).findUserById(userId2);
        verify(repository, never()).save(request);
    }

    @Test
    @DisplayName("addParticipationRequest - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку")
    void addParticipationRequest_ParticipantLimitFull() {
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        final Event expectedEvent = eventList.get(5);
        final Long eventID = expectedEvent.getId();
        when(eventService.findEventById(anyLong())).thenReturn(expectedEvent);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.addParticipationRequest(userId2, eventID));

        assertEquals("Event confirmed limit reached.", exception.getMessage());

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventID, userId2);
        verify(eventService, times(1)).findEventById(eventID);
        verify(userService, never()).findUserById(userId2);
        verify(repository, never()).save(request);
    }

    @Test
    void addParticipationRequest_UserNotExist() {
        when(repository.existsByEventIdAndRequesterId(anyLong(), anyLong())).thenReturn(false);
        when(eventService.findEventById(anyLong())).thenReturn(event);

        final Long userId = 1L;
        when(userService.findUserById(anyLong())).thenThrow(new NotFoundException(
                String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.addParticipationRequest(userId2, eventId));
        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId), exception.getMessage());

        verify(repository, times(1)).existsByEventIdAndRequesterId(eventId, userId2);
        verify(eventService, times(1)).findEventById(eventId);
        verify(userService, times(1)).findUserById(userId2);
        verify(repository, never()).save(new Request());
    }

    @Test
    void getEventParticipants() {
        doNothing().when(userService).checkExistById(userId2);
        when(repository.findAllByEvent_InitiatorIdAndEventId(anyLong(), anyLong())).thenReturn(requestList);

        final List<ParticipationRequestDto> actualParticipants = service.getEventParticipants(userId2, eventId);
        assertEquals(dtoList, actualParticipants);

        verify(userService, times(1)).checkExistById(userId2);
        verify(repository, times(1)).findAllByEvent_InitiatorIdAndEventId(userId2, eventId);
    }

    @Test
    void getEventParticipants_existUser() {
        doThrow(new NotFoundException(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId2)))
                .when(userService).checkExistById(userId2);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getEventParticipants(userId2, eventId));
        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId2), exception.getMessage());

        verify(userService, times(1)).checkExistById(userId2);
        verify(repository, never()).findAllByEvent_InitiatorIdAndEventId(userId2, eventId);
    }

    @Test
    void changeRequestStatus_setCONFIRMED_whenParticipantLimitNot_0() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(CONFIRMED)
                .build();

        final EventRequestStatusUpdateResult expectedRequest = EventRequestStatusUpdateResult.builder()
                .rejectedRequests(List.of())
                .confirmedRequests(dtoRequestListConfirmed)
                .build();

        doNothing().when(userService).checkExistById(1L);
        when(eventService.findEventById(anyLong())).thenReturn(event);
        when(repository.findAllByIdInAndStatus(any(), any())).thenReturn(requestList);
        when(eventRepository.save(any())).thenReturn(event);
        when(repository.saveAll(any())).thenReturn(requestList);

        final EventRequestStatusUpdateResult updatedRequest = service.changeRequestStatus(updateRequest, 1L, eventId);
        assertEquals(expectedRequest, updatedRequest);
    }

    @Test
    void changeRequestStatus_setCONFIRMED2_whenParticipantLimitNot_0() {
        final List<Request> requestListById = List.of(request1, request2);

        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L, 2L))
                .status(CONFIRMED)
                .build();

        final EventRequestStatusUpdateResult expectedRequest = EventRequestStatusUpdateResult.builder()
                .rejectedRequests(dtoRequestListRejected)
                .confirmedRequests(dtoRequestListConfirmed)
                .build();

        final Event event = eventList.get(4);

        doNothing().when(userService).checkExistById(1L);
        when(eventService.findEventById(anyLong())).thenReturn(event);
        when(repository.findAllByIdInAndStatus(any(), any())).thenReturn(requestListById);
        when(eventRepository.save(any())).thenReturn(event);
        when(repository.saveAll(any())).thenReturn(requestListById);

        final EventRequestStatusUpdateResult updatedRequest = service.changeRequestStatus(updateRequest, 1L, eventId);
        assertEquals(expectedRequest, updatedRequest);
    }

    @Test
    void changeRequestStatus_throwException_whenSetCansel() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(CANCELED)
                .build();

        doNothing().when(userService).checkExistById(1L);
        when(eventService.findEventById(anyLong())).thenReturn(event);
        when(repository.findAllByIdInAndStatus(any(), any())).thenReturn(requestList);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.changeRequestStatus(updateRequest, 1L, eventId));
        assertEquals("Wrong status. Status should be one of: [CONFIRMED, REJECTED]",
                exception.getMessage());

        verify(eventRepository, never()).save(event);
        verify(repository, never()).saveAll(requestList);
    }

    @Test
    void changeRequestStatus_throwException_whenSetCONFIRMED_whenParticipantLimitNot_And_ConfirmedRequests() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(2L))
                .status(CONFIRMED)
                .build();

        final Event event = eventList.get(5);
        doNothing().when(userService).checkExistById(anyLong());
        when(eventService.findEventById(anyLong())).thenReturn(event);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.changeRequestStatus(updateRequest, 1L, eventId));
        assertEquals("The limit on confirmations for this event has already been reached.",
                exception.getMessage());
    }

    @Test
    void changeRequestStatus_setREJECTED_whenParticipantLimitNot_0() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(REJECTED)
                .build();

        final EventRequestStatusUpdateResult expectedRequest = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(List.of())
                .rejectedRequests(dtoRequestListRejected)
                .build();

        doNothing().when(userService).checkExistById(1L);
        when(eventService.findEventById(anyLong())).thenReturn(event);
        when(repository.findAllByIdInAndStatus(any(), any())).thenReturn(regectList);
        when(repository.saveAll(any())).thenReturn(regectList);

        final EventRequestStatusUpdateResult updatedRequest = service.changeRequestStatus(updateRequest, 1L, eventId);
        assertEquals(expectedRequest, updatedRequest);

        verify(eventRepository, never()).save(new Event());
    }

    @Test
    void changeRequestStatus_throwException_whenUserNotInitiator() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(REJECTED)
                .build();

        doNothing().when(userService).checkExistById(userId2);
        when(eventService.findEventById(anyLong())).thenReturn(event);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.changeRequestStatus(updateRequest, userId2, eventId));
        assertEquals(String.format("User(id=%d) is not the initiator of the event(id=%d).", userId2, eventId),
                exception.getMessage());

        verify(eventRepository, never()).save(new Event());
    }

    @Test
    void changeRequestStatus_setCONFIRMED_whenParticipantLimit_0() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(CONFIRMED)
                .build();

        final EventRequestStatusUpdateResult expectedRequest = EventRequestStatusUpdateResult.builder()
                .rejectedRequests(List.of())
                .confirmedRequests(dtoRequestListConfirmed)
                .build();

        final Event currentEvent = eventList.get(1);

        doNothing().when(userService).checkExistById(2L);
        when(eventService.findEventById(anyLong())).thenReturn(currentEvent);
        when(repository.findAllByIdInAndStatus(any(), any())).thenReturn(requestList);
        when(repository.saveAll(any())).thenReturn(requestList);

        final EventRequestStatusUpdateResult updatedRequest = service.changeRequestStatus(updateRequest, 2L, 1L);
        assertEquals(expectedRequest, updatedRequest);

        verify(eventRepository, never()).save(new Event());
    }

    @Test
    void changeRequestStatus_setCONFIRMED_whenIsRequestModeration_False() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(CONFIRMED)
                .build();

        final EventRequestStatusUpdateResult expectedRequest = EventRequestStatusUpdateResult.builder()
                .rejectedRequests(List.of())
                .confirmedRequests(dtoRequestListConfirmed)
                .build();

        final Event currentEvent = eventList.get(0);
        currentEvent.setRequestModeration(false);

        doNothing().when(userService).checkExistById(1L);
        when(eventService.findEventById(anyLong())).thenReturn(currentEvent);
        when(repository.findAllByIdInAndStatus(any(), any())).thenReturn(requestList);
        when(repository.saveAll(any())).thenReturn(requestList);

        final EventRequestStatusUpdateResult updatedRequest = service.changeRequestStatus(updateRequest, 1L, 1L);
        assertEquals(expectedRequest, updatedRequest);

        verify(eventRepository, never()).save(new Event());
    }

    @Test
    void changeRequestStatus_throwException_whenEventNotExist() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(CONFIRMED)
                .build();

        doNothing().when(userService).checkExistById(anyLong());
        doThrow(new NotFoundException(String.format(EVENT_WITH_ID_D_WAS_NOT_FOUND, eventId)))
                .when(eventService).findEventById(anyLong());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.changeRequestStatus(updateRequest, userId2, eventId));
        assertEquals(String.format(EVENT_WITH_ID_D_WAS_NOT_FOUND, eventId), exception.getMessage());

        verify(repository, never()).findAllByIdInAndStatus(List.of(), PENDING);
        verify(eventRepository, never()).save(event);
        verify(repository, never()).saveAll(Collections.emptyList());
    }

    @Test
    void changeRequestStatus_throwException_whenNotAllRequestsPending() {
        final EventRequestStatusUpdateRequest updateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L, 3L))
                .status(CONFIRMED)
                .build();

        doNothing().when(userService).checkExistById(anyLong());
        when(eventService.findEventById(anyLong())).thenReturn(event);
        when(repository.findAllByIdInAndStatus(any(), any())).thenReturn(requestList);

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.changeRequestStatus(updateRequest, 1L, eventId));
        assertEquals("Status change is only possible for requests with state='PENDING'", exception.getMessage());

        verify(userService, times(1)).checkExistById(1L);
        verify(eventService, times(1)).findEventById(eventId);
        verify(repository, times(1)).findAllByIdInAndStatus(List.of(1L, 3L), PENDING);
        verify(eventRepository, never()).save(event);
        verify(repository, never()).saveAll(Collections.emptyList());
    }

    @Test
    void getUserRequests() {
        doNothing().when(userService).checkExistById(userId2);
        when(repository.findAllByRequesterId(anyLong())).thenReturn(requestList);

        final List<ParticipationRequestDto> userRequests = service.getUserRequests(userId2);
        assertEquals(dtoList, userRequests);

        verify(userService, times(1)).checkExistById(userId2);
        verify(repository, times(1)).findAllByRequesterId(userId2);
    }

    @Test
    void getUserRequests_throwException_whenUserNotExist() {
        doThrow(new NotFoundException(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId2)))
                .when(userService).checkExistById(userId2);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getUserRequests(userId2));
        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId2), exception.getMessage());

        verify(userService, times(1)).checkExistById(userId2);
        verify(repository, never()).findAllByRequesterId(userId2);
    }

    @Test
    void cancelRequest() {
        request = makeRequest();
        final Request updatedRequest = request.toBuilder().status(CANCELED).build();
        doNothing().when(userService).checkExistById(userId2);
        when(repository.findByIdAndRequesterId(anyLong(), anyLong())).thenReturn(Optional.of(request));
        when(repository.save(any())).thenReturn(updatedRequest);

        final long requestId = 1L;
        service.cancelRequest(userId2, requestId);
    }

    @Test
    void cancelRequest_throwException_whenUserNotExist() {
        doThrow(new NotFoundException(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId2)))
                .when(userService).checkExistById(userId2);
        final long requestId = 1L;

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.cancelRequest(userId2, requestId));
        assertEquals(String.format(USER_WITH_ID_D_WAS_NOT_FOUND, userId2), exception.getMessage());

        verify(userService, times(1)).checkExistById(userId2);
        verify(repository, never()).save(new Request());
    }

    @Test
    void cancelRequest_throwException_whenRequestExist() {
        doNothing().when(userService).checkExistById(userId2);
        when(repository.findByIdAndRequesterId(anyLong(), anyLong())).thenReturn(Optional.empty());

        final long requestId = 1L;
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.cancelRequest(userId2, requestId));
        assertEquals(String.format("Request with id=%d for userId=%d was not found.", requestId, userId2), exception.getMessage());

        verify(userService, times(1)).checkExistById(userId2);
        verify(repository, times(1)).findByIdAndRequesterId(requestId, userId2);
    }
}