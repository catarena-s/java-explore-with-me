package ru.practicum.enums;

import static ru.practicum.mapper.EnumMapper.getEnumFromString;

public enum FriendshipState {
    PENDING, APPROVED, REJECTED;

    public static FriendshipState from(String name) {
        return getEnumFromString(FriendshipState.class, name, "Unknown friendship status");
    }
}
