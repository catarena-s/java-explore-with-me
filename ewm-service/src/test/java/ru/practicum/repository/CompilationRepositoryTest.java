package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static ru.practicum.utils.TestInitDataUtil.getCategoryList;
import static ru.practicum.utils.TestInitDataUtil.getEventList;
import static ru.practicum.utils.TestInitDataUtil.getUserList;

@DataJpaTest
class CompilationRepositoryTest {
    @Autowired
    private CompilationRepository repository;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    private List<Event> eventList;
    private List<User> userList;
    private List<Category> categoryList;
    private List<Compilation> compilations;

    @BeforeEach
    void setUp() {
        userList = getUserList(userRepository);
        categoryList = getCategoryList(categoryRepository);
        eventList = getEventList(eventRepository, locationRepository, categoryList, userList);

        repository.save(Compilation.builder()
                .title("Compilation1")
                .events(eventList)
                .pinned(true)
                .build());
        repository.save(Compilation.builder()
                .title("Compilation2")
                .events(eventList)
                .pinned(false)
                .build());
        repository.save(Compilation.builder()
                .title("Compilation3")
                .events(eventList)
                .pinned(true)
                .build());
    }

    @Test
    void findAllByPinned_whenPinned_True() {
        final PageRequest page = PageRequest.of(0 / 10, 10);
        final List<Compilation> actualList = repository.findAllByPinned(true, page).getContent();
        assertFalse(actualList.isEmpty());
        assertEquals(2, actualList.size());
        assertEquals("Compilation1", actualList.get(0).getTitle());
        assertEquals("Compilation3", actualList.get(1).getTitle());
    }

    @Test
    void findAllByPinned_whenPinned_False() {
        final PageRequest page = PageRequest.of(0 / 10, 10);
        final List<Compilation> actualList = repository.findAllByPinned(false, page).getContent();
        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());
        assertEquals("Compilation2", actualList.get(0).getTitle());
    }
}