package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.model.Location;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class LocationRepositoryTest {
    @Autowired
    private LocationRepository repository;
    private final float lat = 36.5F;
    private final float lon = 15.586F;
    private Location location;

    @BeforeEach
    void setUp() {
        location = repository.save(Location.builder().lat(lat).lon(lon).build());
    }

    @Test
    void findByLatAndLon() {
        final Optional<Location> locationOptional = repository.findByLatAndLon(lat, lon);

        assertEquals(location, locationOptional.get());
    }
}