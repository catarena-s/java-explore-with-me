package ru.practicum.service.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.ResponseException;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StatsServiceTest {
    @Mock
    private StatsClient statsClient;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private StatsServiceImpl service;
    private ViewStatsDto viewStatsDto1;
    private ViewStatsDto viewStatsDto2;
    private final String ip = "localhost:8080";
    private final String uri = "/events/1";

    @BeforeEach
    void setUp() {
        final String appName = "main-service";
        ReflectionTestUtils.setField(service, "appName", appName);

        viewStatsDto1 = ViewStatsDto.builder().app(appName).uri(uri).hits(1).build();
        viewStatsDto2 = ViewStatsDto.builder().app(appName).uri("/events/2").hits(1).build();
    }

    @Test
    void save() {
        final ResponseEntity<Object> response = ResponseEntity.created(URI.create(ip)).build();

        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRemoteAddr()).thenReturn(ip);
        when(statsClient.saveHit(any())).thenReturn(response);

        service.save(request);
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRemoteAddr();
    }

    @Test
    void save_List() {
        final ResponseEntity<Object> response = ResponseEntity.created(URI.create(ip)).build();

        when(request.getRequestURI()).thenReturn("/events");
        when(request.getRemoteAddr()).thenReturn(ip);
        when(statsClient.saveHit(any())).thenReturn(response);

        service.save(request, List.of(1L, 2L));

        verify(request, atLeast(1)).getRequestURI();
        verify(request, atLeast(1)).getRemoteAddr();
    }

    @Test
    void save_BadRequest() {
        final ResponseEntity<Object> response = ResponseEntity.badRequest().build();

        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRemoteAddr()).thenReturn(ip);
        when(statsClient.saveHit(any())).thenReturn(response);

        final ResponseException exception = assertThrows(ResponseException.class, () -> service.save(request));
        assertEquals("Failed to save data to stats service.", exception.getMessage());
    }

    @Test
    void getMap() {
        final ResponseEntity<Object> response = ResponseEntity.ok(List.of(viewStatsDto1));
        final Map<String, Long> expected = Map.of("/events/1", 1L);

        when(request.getRemoteAddr()).thenReturn(ip);
        when(request.getRequestURI()).thenReturn(uri);
        when(statsClient.getStats(any(), any(), ArgumentMatchers.anyList(), anyBoolean())).thenReturn(response);

        final Map<String, Long> actualViewStatsDto = service.getMap(request, true);
        assertEquals(expected, actualViewStatsDto);
    }

    @Test
    void getMap2() {
        final ResponseEntity<Object> response = ResponseEntity.ok(List.of(viewStatsDto1, viewStatsDto2));
        final Map<String, Long> expected = Map.of(
                "/events/1", 1L,
                "/events/2", 1L
        );

        when(request.getRemoteAddr()).thenReturn(ip);
        when(request.getRequestURI()).thenReturn(uri);
        when(statsClient.getStats(any(), any(), ArgumentMatchers.anyList(), anyBoolean())).thenReturn(response);

        final Map<String, Long> actualViewStatsDto = service.getMap(request, List.of(1L, 2L), true);
        assertEquals(expected, actualViewStatsDto);
    }

    @Test
    void getMap3() {
        final ResponseEntity<Object> response = ResponseEntity.ok(List.of(viewStatsDto1, viewStatsDto2));
        final Map<String, Long> expected = Map.of(
                "/events/1", 1L,
                "/events/2", 1L
        );

        when(request.getRemoteAddr()).thenReturn(ip);
        when(request.getRequestURI()).thenReturn(uri);
        when(statsClient.getStats(any(), any(), ArgumentMatchers.anyList(), anyBoolean())).thenReturn(response);

        final LocalDateTime start = LocalDateTime.MIN;
        final LocalDateTime end = LocalDateTime.MAX;
        final Map<String, Long> actualViewStatsDto = service.getMap(request, List.of(1L, 2L), start, end, true);
        assertEquals(expected, actualViewStatsDto);
    }

    @Test
    void get_BadRequest() {
        final ResponseEntity<Object> response = ResponseEntity.badRequest().build();

        when(request.getRemoteAddr()).thenReturn(ip);
        when(request.getRequestURI()).thenReturn(uri);
        when(statsClient.getStats(any(), any(), ArgumentMatchers.anyList(), anyBoolean())).thenReturn(response);

        final ResponseException exception = assertThrows(ResponseException.class,
                () -> service.getMap(request, true));
        assertEquals("Failed to get data from stats service.", exception.getMessage());
    }
}