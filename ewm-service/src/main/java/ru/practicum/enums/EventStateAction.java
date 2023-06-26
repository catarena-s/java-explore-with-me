package ru.practicum.enums;

import lombok.Getter;

@Getter
public enum EventStateAction {
    SEND_TO_REVIEW(EventState.PENDING),
    CANCEL_REVIEW(EventState.CANCELED),
    PUBLISH_EVENT(EventState.PUBLISHED),
    REJECT_EVENT(EventState.CANCELED);

    private final EventState eventState;

    EventStateAction(EventState eventState) {
        this.eventState = eventState;
    }

    public static EventStateAction from(String name) {
        for (EventStateAction esa : values()) {
            if (esa.name().equalsIgnoreCase(name)) {
                return esa;
            }
        }
        throw new IllegalArgumentException("Unknown event state action: " + name);
    }
}
