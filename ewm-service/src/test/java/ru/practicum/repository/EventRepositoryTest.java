package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.enums.EventState;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    private List<Event> eventList;
    private List<User> userList;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        userList = getUserList(userRepository);
        categoryList = getCategoryList(categoryRepository);
        eventList = getEventList(repository, locationRepository, categoryList, userList);
    }

    @Test
    void findByIdAndInitiatorId() {
        final Event event = eventList.get(0);
        long id = event.getId();
        long userId = event.getInitiator().getId();
        final Optional<Event> eventOptional = repository.findByIdAndInitiatorId(id, userId);
        assertEquals(event, eventOptional.get());
    }

    @Test
    void findAllByInitiatorId() {
        final Long userId = userList.get(0).getId();
        final List<Event> expected = List.of(eventList.get(0));
        final PageRequest page = PageRequest.of(0 / 10, 10);
        final Page<Event> eventList = repository.findAllByInitiatorId(userId, page);
        assertEquals(expected, eventList.getContent());
    }

    @Test
    void existsByCategoryId_thenTrue() {
        long catId = categoryList.get(0).getId();
        final boolean exists = repository.existsByCategoryId(catId);
        assertTrue(exists);
    }

    @Test
    void existsByCategoryId_thenFalse() {
        long catId = categoryList.get(categoryList.size() - 1).getId() + 8;
        final boolean exists = repository.existsByCategoryId(catId);
        assertFalse(exists);
    }

    @Test
    void findByIdAndState() {
        final Event event = eventList.get(0);
        long id = event.getId();
        final Optional<Event> eventOptional = repository.findByIdAndState(id, EventState.PENDING);
        assertEquals(event, eventOptional.get());
    }
}