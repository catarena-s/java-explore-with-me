
package ru.practicum.dto.compilation;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Подборка событий
 */
@Data
@Builder
public class NewCompilationDto {
    @NotBlank(message = "Title cannot be null or empty")
    @Size(min = 1, max = 50,message = "size must be between 1 and 50")
    private String title;
    private boolean pinned = false;
    private List<Long> events;
}
