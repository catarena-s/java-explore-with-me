package ru.practicum.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EndpointHitDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHitMapper {

    public static EndpointHit fromDto(EndpointHitDto dto) {
        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}