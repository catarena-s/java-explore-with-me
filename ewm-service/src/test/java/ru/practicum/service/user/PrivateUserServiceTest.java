package ru.practicum.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateUserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final long userId = 1L;
    private User expectedUser;

    @BeforeEach
    void setUp() {
        expectedUser = User.builder()
                .id(userId)
                .name("Name")
                .email("email@mail.ru")
                .build();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeSubscribeMode(boolean isAuto) {
        final User user = expectedUser.toBuilder().autoSubscribe(isAuto).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(user);

        final UserDto actualNewUser = userService.changeSubscribeMode(userId, isAuto);
        assertEquals(UserMapper.toDto(user), actualNewUser);
    }
}