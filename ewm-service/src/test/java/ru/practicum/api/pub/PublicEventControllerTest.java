package ru.practicum.api.pub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.service.event.EventService;
import ru.practicum.utils.TestInitDataUtil;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicEventController.class)
class PublicEventControllerTest {
    @MockBean
    private EventService eventService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private List<EventShortDto> eventFullDtoList;
    private EventFullDto eventShort;

    @BeforeEach
    void setUp() {
        final List<Event> eventList = TestInitDataUtil.getEventList(TestInitDataUtil.getCategoryList(), TestInitDataUtil.getUserList());
        eventFullDtoList = eventList
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
        eventShort = EventMapper.toFullDto(eventList.get(0));

    }

    @Test
    void getPublishedEvent() throws Exception {
        when(eventService.getPublishedEvent(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(eventShort);

        mvc.perform(get("/events/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPublishedEvents() throws Exception {
        when(eventService.getPublishedEvents(anyString(), anyList(), anyBoolean(), any(), any(), anyBoolean(),
                any(), anyInt(), anyInt(), any())
        ).thenReturn(eventFullDtoList);

        mvc.perform(get("/events")
                        .param("text", "text")
                        .param("users", "1", "2")
                        .param("sort","VIEWS")
                        .param("categories", "1", "2")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}