package ru.practicum.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String EVENT_WITH_ID_D_WAS_NOT_FOUND = "Event with id=%d was not found.";
    public static final String CATEGORY_WITH_ID_D_WAS_NOT_FOUND = "Category with id=%d was not found.";
    public static final String USER_WITH_ID_D_WAS_NOT_FOUND = "User with id=%d was not found.";
    public static final String COMPILATION_WITH_ID_WAS_NOT_FOUND = "Compilation with id=%d was not found.";
    public static final String THE_REQUIRED_OBJECT_WAS_NOT_FOUND = "The required object was not found.";
    public static final String INTEGRITY_CONSTRAINT_HAS_BEEN_VIOLATED = "Integrity constraint has been violated.";
    public static final String YOU_CANNOT_S_EVENT_WHEN_CURRENT_STATUS_S = "You cannot %s event when current status %s";

    public static final String FROM = "0";
    public static final String PAGE_SIZE = "10";
    public static final String FOR_THE_REQUESTED_OPERATION_THE_CONDITIONS_ARE_NOT_MET = "For the requested operation the conditions are not met.";
}