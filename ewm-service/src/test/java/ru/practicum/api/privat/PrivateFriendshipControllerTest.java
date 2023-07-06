package ru.practicum.api.privat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.subs.FriendshipDto;
import ru.practicum.dto.subs.FriendshipShortDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.subs.FriendshipService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateFriendshipController.class)
class PrivateFriendshipControllerTest {
    @MockBean
    private FriendshipService service;
    @Autowired
    private MockMvc mvc;
    private FriendshipDto friendshipDto;
    private FriendshipShortDto friendshipShortDto;

    @BeforeEach
    void setUp() {
        friendshipDto = FriendshipDto.builder()
                .followerId(1L)
                .friend(UserShortDto.builder().name("User").build())
                .build();
        friendshipShortDto = FriendshipShortDto.builder()
                .friend(UserShortDto.builder().name("User").build())
                .build();
    }

    @Test
    void requestFriendship() throws Exception {
        when(service.requestFriendship(anyLong(), anyLong())).thenReturn(friendshipDto);

        mvc.perform(post("/users/{userId}/friendships/{friendId}", 1, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteFriendshipRequest() throws Exception {
        doNothing().when(service).deleteFriendshipRequest(anyLong(), anyLong());

        mvc.perform(delete("/users/{userId}/friendships/{subsId}", 1, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void approveFriendship() throws Exception {
        when(service.approveFriendship(anyLong(), any())).thenReturn(List.of(friendshipShortDto));

        mvc.perform(patch("/users/{userId}/friendships/approve", 1)
                        .param("ids", "1", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void rejectFriendship() throws Exception {
        when(service.rejectFriendship(anyLong(), any())).thenReturn(List.of(friendshipShortDto));

        mvc.perform(patch("/users/{userId}/friendships/approve", 1)
                        .param("ids", "1", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getFriendshipRequests() throws Exception {
        when(service.getFriendshipRequests(anyLong(), anyString())).thenReturn(List.of(friendshipShortDto));

        mvc.perform(get("/users/{userId}/friendships/requests", 1)
                        .param("filter", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getIncomingFriendRequests() throws Exception {
        when(service.getIncomingFriendRequests(anyLong(), anyString())).thenReturn(List.of(friendshipShortDto));

        mvc.perform(get("/users/{userId}/friendships/requests", 1)
                        .param("filter", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}