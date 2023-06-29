package ru.practicum.api.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.utils.Constants.FROM;
import static ru.practicum.utils.Constants.PAGE_SIZE;

@RestController
@RequestMapping(path = "/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    /**
     * В случае, если подборки с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        log.debug("Request received GET /compilations/{}", compId);
        return compilationService.getCompilation(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @PositiveOrZero @RequestParam(value = "from", defaultValue = FROM) Integer from,
                                                @Positive @RequestParam(value = "size", defaultValue = PAGE_SIZE) Integer size) {
        log.debug("Request received GET /compilations");
        log.debug("RequestParams: pinned={},from={},size={}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }

}
