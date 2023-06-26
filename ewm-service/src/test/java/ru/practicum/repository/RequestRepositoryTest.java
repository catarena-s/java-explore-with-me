package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Category;
import ru.practicum.utils.TestInitDataUtil;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RequestRepositoryTest {
    @Autowired
    private RequestRepository repository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    private List<User> users = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Request> requestList = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        users = TestInitDataUtil.getUserList(userRepository);
        final List<Category> categories = TestInitDataUtil.getCategoryList(categoryRepository);
        events = TestInitDataUtil.getEventList(eventRepository, locationRepository, categories, users);
        requestList = TestInitDataUtil.getRequestList(repository, users, events);
    }

    @Test
    void findByIdAndRequesterId() {
        final Long userId = users.get(0).getId();
        final Long requestId = requestList.get(0).getId();
        final Request actualRequest = repository.findByIdAndRequesterId(requestId, userId).get();
        assertEquals(requestList.get(0), actualRequest);
    }

    @Test
    void findAllByRequesterId() {
        final Long userId = users.get(1).getId();
        final List<Request> actualRequests = repository.findAllByRequesterId(userId);
        assertEquals(List.of(requestList.get(1)), actualRequests);
    }

    @Test
    void findAllByIdInAndStatus() {
        final Long id1 = requestList.get(0).getId();
        final Long id2 = requestList.get(1).getId();
        final List<Request> actualRequests = repository.findAllByIdInAndStatus(List.of(id1, id2), RequestStatus.CONFIRMED);
        assertEquals(List.of(requestList.get(1)), actualRequests);
    }

    @Test
    void findAllByInitiatorIdAndEventId() {
        final Long userId = users.get(1).getId();
        final long eventId = events.get(1).getId();
        final List<Request> actualRequests = repository.findAllByEvent_InitiatorIdAndEventId(userId, eventId);
        assertEquals(List.of(requestList.get(0)), actualRequests);
    }

    @Test
    void findAllByInitiatorIdAndEventId_thenEmpty() {
        final Long userId = users.get(2).getId();
        final long eventId = events.get(1).getId();
        final List<Request> actualRequests = repository.findAllByEvent_InitiatorIdAndEventId(userId, eventId);
        assertEquals(Collections.emptyList(), actualRequests);
    }

    @Test
    void existsByEventIdAndRequesterId() {
        final Long userId = users.get(1).getId();
        final long eventId = events.get(0).getId();
        final boolean existsRes = repository.existsByEventIdAndRequesterId(eventId, userId);
        assertTrue(existsRes);
    }

    @Test
    void existsByEventIdAndRequesterId_NotExist() {
        final Long userId = users.get(2).getId();
        final long eventId = events.get(0).getId();
        final boolean existsRes = repository.existsByEventIdAndRequesterId(eventId, userId);
        assertFalse(existsRes);
    }
}