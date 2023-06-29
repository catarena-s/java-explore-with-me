package ru.practicum.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import static ru.practicum.mapper.EnumMapper.getEnumFromString;

public enum RequestStatus {
    PENDING, CONFIRMED, REJECTED, CANCELED;

    @JsonCreator
    public static RequestStatus from(String name) {
        return getEnumFromString(RequestStatus.class, name, "Unknown request status");
    }
}
