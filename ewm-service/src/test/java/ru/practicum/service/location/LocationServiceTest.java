package ru.practicum.service.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Location;
import ru.practicum.repository.LocationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {
    @Mock
    private LocationRepository repository;
    @InjectMocks
    private LocationServiceImpl locationService;
    private LocationDto locationDto;
    private Location expectedLocation;
    private final float lat = 37.62f;
    private final float lon = 55.754167f;

    @BeforeEach
    void setUp() {
        locationDto = new LocationDto(lat, lon);
        expectedLocation = new Location(1L, lat, lon);
    }

    @Test
    void findLocation() {
        when(repository.findByLatAndLon(anyFloat(), anyFloat())).thenReturn(Optional.of(expectedLocation));

        final Location actualLocation = locationService.findLocation(locationDto);
        assertEquals(expectedLocation, actualLocation);

        verify(repository, times(1)).findByLatAndLon(lat, lon);
        verify(repository, never()).save(expectedLocation);
    }

    @Test
    void findLocation_whenLocationNotExist() {
        when(repository.findByLatAndLon(anyFloat(), anyFloat())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(expectedLocation);

        final Location actualLocation = locationService.findLocation(locationDto);
        assertEquals(expectedLocation, actualLocation);

        verify(repository, times(1)).findByLatAndLon(lat, lon);
        verify(repository, times(1)).save(LocationMapper.fromDto(locationDto));
    }

}