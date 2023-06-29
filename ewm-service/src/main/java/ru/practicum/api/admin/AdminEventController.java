package ru.practicum.api.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventService;
import ru.practicum.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.Constants.YYYY_MM_DD_HH_MM_SS;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(
            //список id пользователей, чьи события нужно найти
            @RequestParam(value = "users", required = false) List<Long> users,
            //список состояний в которых находятся искомые события
            @RequestParam(value = "states", required = false) List<String> states,
            //список id категорий в которых будет вестись поиск
            @RequestParam(value = "categories", required = false) List<Long> categories,
            //дата и время не раньше которых должно произойти событие
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeStart,
            //дата и время не позже которых должно произойти событие
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeEnd,
            //количество событий, которые нужно пропустить для формирования текущего набора
            @PositiveOrZero @RequestParam(value = "from", defaultValue = Constants.FROM) Integer from,
            //количество событий в наборе
            @Positive @RequestParam(value = "size", defaultValue = Constants.PAGE_SIZE) Integer size
    ) {
        log.debug("Request received GET /admin/events");
        log.debug("RequestParams: users={},states={},categories={},rangeStart={}, rangeEnd={}, from={}, size={} ",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@Valid @RequestBody(required = false) UpdateEventAdminRequest body,
                                           @PathVariable long eventId) {
        log.debug("Request received PATCH /admin/events/{}:{}", eventId, body);
        return eventService.updateEventByAdmin(body, eventId);
    }
}
