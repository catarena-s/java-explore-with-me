package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.Constants.YYYY_MM_DD_HH_MM_SS;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHitDto {
    @NotBlank(message = "App cannot be empty or null")
    private String app;
    @NotBlank(message = "Uri cannot be empty or null")
    private String uri;
    @NotBlank(message = "Ip cannot be empty or null")
    private String ip;
    @NotNull(message = "Timestamp cannot be empty or null")
    @JsonFormat(pattern = YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime timestamp;
}