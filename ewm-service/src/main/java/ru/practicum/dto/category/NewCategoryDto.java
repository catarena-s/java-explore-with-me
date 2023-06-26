package ru.practicum.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Данные для добавления новой категории
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Category name cannot be empty or null")
    @Size(max = 50, message = "size must be between 0 and 50")
    private String name;
}
