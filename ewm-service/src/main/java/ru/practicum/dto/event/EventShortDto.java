package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.Constants.YYYY_MM_DD_HH_MM_SS;

/**
 * Краткая информация о событии
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private Long views = 0L;
}
