package ru.practicum.api.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.user.UserService;
import ru.practicum.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@Valid @RequestBody NewUserRequest body) {
        log.debug("Request received POST /admin/users : {}", body);
        return userService.registerUser(body);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                  @PositiveOrZero
                                  @RequestParam(value = "from", defaultValue = Constants.FROM) Integer from,
                                  @Positive
                                  @RequestParam(value = "size", defaultValue = Constants.PAGE_SIZE) Integer size) {
        if (ids != null) {
            log.debug("Request received GET /admin/users?{}&from={}&size={}",
                    ids.stream().map(aLong -> "ids=" + aLong).collect(Collectors.joining("&")),
                    from, size);
        } else {
            log.debug("Request received GET /admin/users?from={}&size={}", from, size);
        }
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "userId") long userId) {
        log.debug("Request received DELETE /admin/users{}", userId);
        userService.delete(userId);
    }
}
