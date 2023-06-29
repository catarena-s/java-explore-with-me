package ru.practicum.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.enums.RequestStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.utils.TestValidatorUtil.hasErrorMessage;

class EventRequestStatusUpdateRequestTest {
    @Test
    void create_withOutRequestList() {
        final EventRequestStatusUpdateRequest requestForUpdateStatus = EventRequestStatusUpdateRequest.builder()
                .status(RequestStatus.CONFIRMED)
                .build();
        assertTrue(hasErrorMessage(requestForUpdateStatus, "RequestId list cannot be null."));
    }

    @Test
    void create_withEmptyRequestList() {
        final EventRequestStatusUpdateRequest requestForUpdateStatus = EventRequestStatusUpdateRequest.builder()
                .requestIds(Collections.emptyList())
                .status(RequestStatus.CONFIRMED)
                .build();
        assertTrue(hasErrorMessage(requestForUpdateStatus, "RequestId list cannot be empty."));
    }

    @ParameterizedTest
    @NullSource
    void create_withEmptyStatus(RequestStatus status) {
        final EventRequestStatusUpdateRequest requestForUpdateStatus = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(status)
                .build();
        assertTrue(hasErrorMessage(requestForUpdateStatus, "Request status cannot be null or empty."));
    }


}
