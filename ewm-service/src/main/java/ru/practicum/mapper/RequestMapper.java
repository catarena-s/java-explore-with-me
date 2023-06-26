package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import static ru.practicum.Constants.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toDto(Request newRequest) {
        return ParticipationRequestDto.builder()
                .id(newRequest.getId())
                .requester(newRequest.getRequester().getId())
                .event(newRequest.getEvent().getId())
                .status(newRequest.getStatus())
                .created(newRequest.getCreated().format(FORMATTER))
                .build();
    }
}
