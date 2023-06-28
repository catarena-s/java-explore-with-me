package ru.practicum.api.privat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.utils.Constants.FROM;
import static ru.practicum.utils.Constants.PAGE_SIZE;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@Valid @RequestBody NewEventDto body,
                                  @PathVariable long userId) {
        log.debug("Request received POST /users/{}/events : {}", userId, body);
        return eventService.saveEvent(userId, body);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable(value = "userId") long userId,
                                 @PathVariable(value = "eventId") long eventId) {
        log.debug("Request received GET /users/{}/events/{}", userId, eventId);
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@Valid @RequestBody UpdateEventUserRequest body,
                                          @PathVariable(value = "userId") long userId,
                                          @PathVariable(value = "eventId") long eventId) {
        log.debug("Request received PATCH /users/{}/events/{} : {}", userId, eventId, body);
        return eventService.updateEventByUser(body, userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable(value = "userId") long userId,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = FROM) int from,
                                         @Positive @RequestParam(value = "size", defaultValue = PAGE_SIZE) int size) {
        log.debug("Request received GET /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }
}
