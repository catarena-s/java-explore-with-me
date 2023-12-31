package ru.practicum.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import static ru.practicum.mapper.EnumMapper.getEnumFromString;

@Getter
public enum SortType {
    EVENT_DATE("eventDate"),
    VIEWS("view");

    private final String name;

    SortType(String name) {
        this.name = name;
    }

    @JsonCreator
    public static SortType from(String name) {
        return getEnumFromString(SortType.class, name,"Unknown sort type");
    }
}
