package ru.practicum.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "RequestId list cannot be null.")
    @NotEmpty(message = "RequestId list cannot be empty.")
    private List<Long> requestIds;
    @NotNull
    @NotBlank(message = "Request status cannot be null or empty.")
    private String status;
}
