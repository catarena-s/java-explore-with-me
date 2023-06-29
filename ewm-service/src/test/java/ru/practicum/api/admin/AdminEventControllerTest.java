package ru.practicum.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.service.event.EventService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;

@WebMvcTest(controllers = AdminEventController.class)
class AdminEventControllerTest {
    @MockBean
    private EventService eventService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto;
    private List<EventFullDto> eventFullDtoList;
    private CategoryDto categoryDto;
    private EventFullDto eventFullDto;
    UpdateEventAdminRequest updateDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .build();
        categoryDto = CategoryDto.builder()
                .name("Cinema")
                .build();


        eventFullDtoList = getEventList(getCategoryList(), getUserList())
                .stream()
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());

        updateDto = UpdateEventAdminRequest.builder()
                .description("New description133213131313313")
                .build();
        eventFullDto = eventFullDtoList.get(0);
        eventFullDto.setDescription(updateDto.getDescription());

    }

    @Test
    void getAdminEvents() throws Exception {
        when(eventService.getEventsByAdmin(any(), anyList(), any(),
                any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt()))
                .thenReturn(eventFullDtoList);

        mvc.perform(get("/admin/events")
                        .param("users", "1", "2")
//                        .param("states","1","2")
                        .param("categories", "1", "2")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
                /*.andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))*/;
    }

    @Test
    void updateEventByAdmin() throws Exception {
        when(eventService.updateEventByAdmin(any(), anyLong()))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())));
    }
}