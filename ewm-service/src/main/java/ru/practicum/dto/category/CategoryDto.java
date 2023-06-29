package ru.practicum.dto.category;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Категория
 */
@Data
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank(message = "Category name cannot be empty or null")
    @Size(min = 1, max = 50, message = "size must be between 1 and 50")
    private String name;
}
