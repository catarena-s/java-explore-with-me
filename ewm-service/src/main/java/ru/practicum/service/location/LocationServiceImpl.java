package ru.practicum.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Location;
import ru.practicum.repository.LocationRepository;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public Location findLocation(LocationDto body) {
        return locationRepository.findByLatAndLon(body.getLat(), body.getLon())
                .orElseGet(() -> saveLocation(body));
    }

    private Location saveLocation(LocationDto body) {
        return locationRepository.save(LocationMapper.fromDto(body));
    }
}
