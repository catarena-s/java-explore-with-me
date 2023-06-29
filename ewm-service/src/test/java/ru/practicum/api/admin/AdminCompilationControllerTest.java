package ru.practicum.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.service.compilation.CompilationService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;

@WebMvcTest(controllers = AdminCompilationController.class)
class AdminCompilationControllerTest {

    @MockBean
    private CompilationService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto;
    private List<EventFullDto> eventFullDtoList;
    private CategoryDto categoryDto;
    private CompilationDto dto;
    NewCompilationDto newCompilationDto;

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

        final List<Event> eventList = getEventList(getCategoryList(), getUserList());
        final List<EventShortDto> eventshortDtos = eventList.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
        dto = CompilationDto.builder()
                .title("Title").events(eventshortDtos).pinned(true)
                .build();
        newCompilationDto = NewCompilationDto.builder()
                .title("Title").pinned(true).events(List.of(1L, 2L))
                .build();
    }

    @Test
    void saveCompilation() throws Exception {
        when(service.saveCompilation(any())).thenReturn(dto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                /*.andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))*/;
    }

    @Test
    void updateCompilation() throws Exception {
        when(service.updateCompilation(any(), anyLong())).thenReturn(dto);

        mvc.perform(patch("/admin/compilations/{compId}", 1)
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                /*.andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))*/;
    }

    @Test
    void deleteCompilation() throws Exception {
        doNothing().when(service).delete(anyLong());

        mvc.perform(MockMvcRequestBuilders.delete("/admin/compilations/{compId}", 1))
                .andExpect(status().isNoContent());
    }
}