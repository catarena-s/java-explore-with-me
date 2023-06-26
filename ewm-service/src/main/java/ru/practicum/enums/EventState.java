package ru.practicum.enums;

import lombok.Getter;

@Getter
public enum EventState {
    CANCELED,
    PENDING,
    PUBLISHED;

    public static EventState from(String name) {
        for (EventState eventState : values()) {
            if (eventState.name().equalsIgnoreCase(name)) {
                return eventState;
            }
        }
        throw new IllegalArgumentException("Unknown event state: " + name);
    }
}
