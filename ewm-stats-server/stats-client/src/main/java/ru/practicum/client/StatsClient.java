package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.Constants.FORMATTER;
import static ru.practicum.Constants.HIT_ENDPOINT;
import static ru.practicum.Constants.STATS_ENDPOINT;

@Service
@Slf4j
public class StatsClient {
    private final String serverUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public StatsClient(@Value("${ewm-stats-server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        final List<String> path = new ArrayList<>();
        final Map<String, Object> parameters = new HashMap<>();

        if (start != null) {
            parameters.put("start", start.format(FORMATTER));
            path.add("start={start}");
        }
        if (end != null) {
            parameters.put("end", end.format(FORMATTER));
            path.add("end={end}");
        }
        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
            path.add("uris={uris}");
        }

        parameters.put("unique", unique);
        path.add("unique={unique}");

        final String url = serverUrl + STATS_ENDPOINT + "?" + String.join("&", path);
        return restTemplate.getForEntity(url, Object.class, parameters);
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto dto) {
        return restTemplate.postForEntity(serverUrl + HIT_ENDPOINT, dto, Object.class);
    }
}
