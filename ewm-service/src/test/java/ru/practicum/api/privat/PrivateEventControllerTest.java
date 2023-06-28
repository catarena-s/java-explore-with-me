package ru.practicum.api.privat;

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
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.service.event.EventService;
import ru.practicum.utils.TestInitDataUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.Constants.FORMATTER;

@WebMvcTest(controllers = PrivateEventController.class)
class PrivateEventControllerTest {
    @MockBean
    private EventService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private List<EventShortDto> eventShortDtoList;
    private NewEventDto newEventDto;
    private EventFullDto expectedDto;
    private UpdateEventUserRequest updateRequestDto;
    private EventFullDto updatedDto;

    @BeforeEach
    void setUp() {
        final List<Event> eventList = TestInitDataUtil.getEventList(TestInitDataUtil.getCategoryList(), TestInitDataUtil.getUserList());
        eventShortDtoList = eventList
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
        expectedDto = EventMapper.toFullDto(eventList.get(0));
        updateRequestDto = UpdateEventUserRequest.builder()
                .annotation("new Annotation after Update")
                .build();
        updatedDto = expectedDto.toBuilder()
                .annotation(updateRequestDto.getAnnotation())
                .build();
        newEventDto = NewEventDto.builder()
                .title("Знаменитое шоу 'Летающая кукуруза'")
                .annotation("Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории")
                .description("Что получится, если соединить кукурузу и полёт? Создатели 'Шоу летающей кукурузы'" +
                        " испытали эту идею на практике и воплотили в жизнь инновационный проект, предлагающий свежий взгляд на развлечения...")
                .eventDate(LocalDateTime.parse("2024-12-31 15:10:05", FORMATTER))
                .paid(true)
                .participantLimit(10)
                .location(new LocationDto(37.62f, 55.754167f))
                .requestModeration(true)
                .category(2L)
                .build();
    }

    @Test
    void addEvent() throws Exception {
        when(service.saveEvent(anyLong(), any())).thenReturn(expectedDto);

        mvc.perform(post("/users/{userId}/events/", 1L)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void getEvent() throws Exception {
        when(service.getEvent(anyLong(), anyLong())).thenReturn(expectedDto);

        mvc.perform(get("/users/{userId}/events/{eventId}", 1L, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateEvent() throws Exception {
        when(service.updateEventByUser(any(), anyLong(), anyLong())).thenReturn(updatedDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}", 1L, 1L)
                        .content(mapper.writeValueAsString(updateRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEvents() throws Exception {
        when(service.getEvents(anyLong(), anyInt(), anyInt())).thenReturn(eventShortDtoList);

        mvc.perform(get("/users/{userId}/events/", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}