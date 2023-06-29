package ru.practicum.api.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.utils.Constants.FROM;
import static ru.practicum.utils.Constants.PAGE_SIZE;

@RestController
@RequestMapping(path = "/")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = FROM) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = PAGE_SIZE) Integer size
    ) {
        log.debug("Request received GET /categories?from={}&size={}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        log.debug("Request received GET /categories/{}", catId);
        return categoryService.getCategory(catId);
    }
}
