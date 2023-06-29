package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.utils.TestInitDataUtil;
import ru.practicum.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    private List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        users = TestInitDataUtil.getUserList(repository);
    }

    @Test
    void testFindAllByIdIn() {
        final List<Long> ids = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
        final PageRequest page = PageRequest.of(0 / 10, 10);
        final List<User> actualUsers = repository.findAllByIdIn(ids, page).getContent();
        assertEquals(users, actualUsers);
    }
}