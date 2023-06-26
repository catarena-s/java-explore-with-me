package ru.practicum.enums;

public enum RequestStatus {
    PENDING, CONFIRMED, REJECTED, CANCELED;

    public static RequestStatus from(String name) {
        for (RequestStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown request status: " + name);
    }
}
