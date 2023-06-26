package ru.practicum.service.request;

import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addParticipationRequest(long userId, long eventId);

    List<ParticipationRequestDto> getEventParticipants(long userId, long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(EventRequestStatusUpdateRequest body, long userId, long eventId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
