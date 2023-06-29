package ru.practicum.api.privat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.service.request.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.Constants.FORMATTER;

@WebMvcTest(controllers = PrivateRequestController.class)
class PrivateRequestControllerTest {
    @MockBean
    private RequestService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final long userId = 2L;
    private final long eventId = 1L;
    private final Long requestId = 1L;
    private ParticipationRequestDto requestDto1;
    private ParticipationRequestDto requestDto2;
    private EventRequestStatusUpdateResult updatedRequest;
    private EventRequestStatusUpdateRequest eventRequest;

    @BeforeEach
    void setUp() {
        requestDto1 = ParticipationRequestDto.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .event(eventId)
                .requester(userId)
                .created(LocalDateTime.now().format(FORMATTER))
                .build();
        requestDto2 = ParticipationRequestDto.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .event(2L)
                .requester(2L)
                .created(LocalDateTime.now().format(FORMATTER))
                .build();
        eventRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L, 2L))
                .status(RequestStatus.CONFIRMED)
                .build();
        updatedRequest = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(List.of(requestDto1, requestDto2))
                .rejectedRequests(List.of())
                .build();
    }

    @Test
    void addParticipationRequest() throws Exception {
        when(service.addParticipationRequest(anyLong(), anyLong())).thenReturn(requestDto1);

        mvc.perform(post("/users/{userId}/requests", userId)
                        .param("eventId", String.valueOf(eventId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void changeRequestStatus() throws Exception {
        when(service.changeRequestStatus(any(), anyLong(), anyLong()))
                .thenReturn(updatedRequest);

        mvc.perform(patch("/users/{userId}/events/{eventId}/requests/", userId, eventId)
                        .content(mapper.writeValueAsString(eventRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventParticipants() throws Exception {
        when(service.getEventParticipants(anyLong(), anyLong()))
                .thenReturn(List.of(requestDto1, requestDto2));

        mvc.perform(get("/users/{userId}/events/{eventId}/requests/", userId, eventId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUserRequests() throws Exception {
        when(service.getUserRequests(anyLong())).thenReturn(List.of(requestDto1, requestDto2));

        mvc.perform(get("/users/{userId}/requests/", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cancelRequest() throws Exception {
        when(service.cancelRequest(anyLong(), anyLong()))
                .thenReturn(requestDto1);

        mvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", userId, requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}