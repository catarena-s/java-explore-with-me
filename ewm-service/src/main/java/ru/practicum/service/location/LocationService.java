package ru.practicum.service.location;

import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Location;

public interface LocationService {

    Location findLocation(LocationDto location);
}
