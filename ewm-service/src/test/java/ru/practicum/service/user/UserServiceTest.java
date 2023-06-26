package ru.practicum.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repository.UserRepository;
import ru.practicum.model.User;
import ru.practicum.utils.Constants;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userUtilService;
    private final long userId = 1L;
    private User expectedUser;
    private String expectedErrMessage;

    @BeforeEach
    void setUp() {
        expectedUser = User.builder()
                .name("Name")
                .email("email@mail.ru")
                .build();
        expectedErrMessage = String.format(Constants.USER_WITH_ID_D_WAS_NOT_FOUND, userId);
    }

    @Test
    void checkExistById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        userUtilService.checkExistById(userId);

        verify(userRepository, times(1)).existsById(userId);
    }


    @Test
    void checkExistById_ThrowExeption() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userUtilService.checkExistById(userId));

        assertEquals(expectedErrMessage, exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void findUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        final User actualUser = userUtilService.findUserById(userId);
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserById_whenUserNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userUtilService.findUserById(userId));
        assertEquals(expectedErrMessage, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}