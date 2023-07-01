package ru.practicum.api.privat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.service.subs.FriendService;
import ru.practicum.utils.TestInitDataUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateFriendController.class)
class PrivateFriendControllerTest {
    @MockBean
    private FriendService service;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto;
    private List<EventShortDto> eventShortDtoList;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .isAutoSubscribe(true)
                .build();

        final List<Event> eventList = TestInitDataUtil.getEventList();
        eventShortDtoList = eventList.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    @Test
    void getFriends() throws Exception {
        when(service.getFriends(anyLong())).thenReturn(List.of(userDto));

        mvc.perform(get("/users/{userId}/friends", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getFollowers() throws Exception {
        when(service.getFollowers(anyLong())).thenReturn(List.of(userDto));

        mvc.perform(get("/users/{userId}/followers", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getParticipateEvents() throws Exception {
        when(service.getParticipateEvents(anyLong(), anyInt(), anyInt())).thenReturn(eventShortDtoList);

        mvc.perform(get("/users/{userId}/friends/share", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getFriendEvents() throws Exception {
        when(service.getFriendEvents(anyLong(), anyInt(), anyInt())).thenReturn(eventShortDtoList);

        mvc.perform(get("/users/{userId}/friends/share", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}