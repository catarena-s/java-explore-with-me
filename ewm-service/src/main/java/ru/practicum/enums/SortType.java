package ru.practicum.enums;

import lombok.Getter;

@Getter
public enum SortType {
    EVENT_DATE("eventDate"),
    VIEWS("view");

    private final String name;

    SortType(String name) {
        this.name = name;
    }

    public static SortType from(String name) {
        for (SortType sortType : values()) {
            if (sortType.name().equalsIgnoreCase(name)) {
                return sortType;
            }
        }
        throw new IllegalArgumentException("Unknown sort type: " + name);
    }
}
