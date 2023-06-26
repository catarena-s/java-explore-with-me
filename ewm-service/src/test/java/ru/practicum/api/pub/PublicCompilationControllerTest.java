package ru.practicum.api.pub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.compilation.CompilationService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicCompilationController.class)
class PublicCompilationControllerTest {

    @MockBean
    private CompilationService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private CompilationDto dto;

    @BeforeEach
    void setUp() {
        final EventShortDto eventShortDto = EventShortDto.builder()
                .id(1L)
                .title("Title")
                .annotation("Anotation")
                .category(new CategoryDto(1L, "Category"))
                .eventDate(LocalDateTime.now().plusMonths(2))
                .initiator(new UserShortDto(1L, "user"))
                .paid(true)
                .confirmedRequests(1)
                .views(1L)
                .build();
        dto = CompilationDto.builder()
                .title("Compilation")
                .pinned(true)
                .events(List.of(eventShortDto))
                .build();
    }

    @Test
    void getCompilation() throws Exception {
        when(service.getCompilation(anyLong())).thenReturn(dto);

        mvc.perform(get("/compilations/{compId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCompilations() throws Exception {
        when(service.getCompilations(anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}