package ru.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.enums.RequestStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "RequestId list cannot be null.")
    @NotEmpty(message = "RequestId list cannot be empty.")
    private List<Long> requestIds;
    @NotNull(message = "Request status cannot be null or empty.")
    private RequestStatus status;
}
