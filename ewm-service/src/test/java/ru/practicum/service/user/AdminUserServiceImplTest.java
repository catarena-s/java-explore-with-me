package ru.practicum.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.utils.Constants;
import ru.practicum.utils.TestInitDataUtil;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {
    @Mock
    private UserRepository repository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private UserServiceImpl service;
    private List<User> userList;
    private List<Long> ids;
    private List<UserDto> userDtoList;
    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        userList = TestInitDataUtil.getUserList();
        userDtoList = userList.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        ids = userList.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Test
    void registerUser() {
        final NewUserRequest body = NewUserRequest.builder().name("Jon").email("jon@mail.com").build();
        final UserDto dto = UserDto.builder().id(1L).name("Jon").email("jon@mail.com").build();
        final User user = TestInitDataUtil.makeUser(1L, "Jon", "jon@mail.com");
        when(repository.save(any())).thenReturn(user);

        final UserDto actualNewUser = service.registerUser(body);
        assertEquals(dto, actualNewUser);
    }

    @Test
    void registerUser_ThrowException() {
        final NewUserRequest newUserRequest = NewUserRequest.builder().name("Jon").email("jon@mail.com").build();
        when(repository.save(any())).thenThrow(DataIntegrityViolationException.class);

        final ConflictException asserted =
                assertThrows(ConflictException.class, () -> service.registerUser(newUserRequest));

        assertEquals("User with email='jon@mail.com' already exists", asserted.getMessage());
    }

    @Test
    void getUsers() {
        when(repository.findAllByIdIn(any(), any())).thenReturn(new PageImpl<>(userList));

        final List<UserDto> actualUserDtoList = service.getUsers(ids, from, size);
        assertEquals(actualUserDtoList, userDtoList);
    }

    @Test
    void getUsers_withNullUserList() {
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(userList));

        final List<UserDto> actualUserDtoList = service.getUsers(null, from, size);
        assertEquals(actualUserDtoList, userDtoList);
    }

    @Test
    void getUsers_withEmptyUserList() {
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(userList));

        final List<UserDto> actualUserDtoList = service.getUsers(Collections.emptyList(), from, size);
        assertEquals(actualUserDtoList, userDtoList);
    }

    @Test
    void delete_whenUserExist() {
        final long userId = 1L;
        when(repository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsByInitiatorId(anyLong())).thenReturn(false);
        when(requestRepository.existsByRequesterId(anyLong())).thenReturn(false);
        doNothing().when(repository).deleteById(anyLong());

        service.delete(userId);

        verify(repository, times(1)).deleteById(userId);
    }

    @Test
    void delete_whenUserNotExists_throwException() {
        when(repository.existsById(anyLong())).thenReturn(false);

        final long userId = 1L;
        final NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> service.delete(userId));

        assertEquals(String.format(Constants.USER_WITH_ID_D_WAS_NOT_FOUND, userId), actualException.getMessage());

        verify(repository, never()).deleteById(userId);
    }

}