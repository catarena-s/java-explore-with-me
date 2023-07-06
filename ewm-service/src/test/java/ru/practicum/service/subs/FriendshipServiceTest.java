package ru.practicum.service.subs;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.subs.FriendshipDto;
import ru.practicum.dto.subs.FriendshipShortDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.FriendshipMapper;
import ru.practicum.model.Friendship;
import ru.practicum.model.User;
import ru.practicum.repository.FriendshipRepository;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static ru.practicum.enums.FriendshipState.APPROVED;
import static ru.practicum.enums.FriendshipState.PENDING;
import static ru.practicum.enums.FriendshipState.REJECTED;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {
    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private FriendshipServiceImpl service;
    private List<FriendshipShortDto> allDto;
    private List<Friendship> all;
    private List<Friendship> pending;
    private List<FriendshipShortDto> pendingDto;
    private List<Friendship> reject;
    private List<FriendshipShortDto> rejectDto;
    private List<Friendship> approved;
    private List<FriendshipShortDto> approvedDto;

    @BeforeEach
    void setUp() {
        final User user1 = new User(1L, "User1", "user1@m.ru", false);
        final User follower1 = new User(2L, "Follower1", "follower1@m.ru", false);
        final User follower2 = new User(3L, "Follower2", "follower2@m.ru", false);
        final Friendship f1 = Friendship.builder().id(2L).friend(user1).follower(follower1).state(PENDING).build();
        final FriendshipShortDto fS1 = FriendshipMapper.toShortDto(f1);
        final Friendship f2 = Friendship.builder().id(3L).friend(user1).follower(follower2).state(PENDING).build();
        final FriendshipShortDto fS2 = FriendshipMapper.toShortDto(f2);
        final Friendship f3 = Friendship.builder().id(4L).friend(user1).follower(follower1).state(REJECTED).build();
        final FriendshipShortDto fS3 = FriendshipMapper.toShortDto(f3);
        final Friendship f4 = Friendship.builder().id(5L).friend(user1).follower(follower2).state(APPROVED).build();
        final FriendshipShortDto fS4 = FriendshipMapper.toShortDto(f4);
        all = List.of(f1, f2);
        pending = List.of(f1, f2);
        reject = List.of(f3);
        approved = List.of(f4);
        allDto = List.of(fS1, fS2);
        pendingDto = List.of(fS1, fS2);
        rejectDto = List.of(fS3);
        approvedDto = List.of(fS4);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void requestFriendship(boolean isAutoSubscribe) {
        final User follower = new User(1L, "User1", "user1@m.ru", false);
        final User friend = new User(2L, "User2", "user2@m.ru", isAutoSubscribe);
        final Friendship friendshipRequest = Friendship.builder()
                .follower(follower)
                .friend(friend)
                .state(friend.isAutoSubscribe() ? APPROVED : PENDING)
                .createdOn(LocalDateTime.now())
                .build();
        ;
        when(userService.findUserById(1L)).thenReturn(follower);
        when(userService.findUserById(2L)).thenReturn(friend);
        when(friendshipRepository.save(any())).thenReturn(friendshipRequest);

        final FriendshipDto friendshipDto = service.requestFriendship(1L, 2L);

        assertEquals(FriendshipMapper.toDto(friendshipRequest), friendshipDto);
    }

    @Test
    void requestFriendship_throwException() {
        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.requestFriendship(1L, 1L));

        assertEquals("You can't follow yourself.", exception.getMessage());
    }

    @Test
    void approveFriendship() {
        final User user1 = new User(1L, "User1", "user1@m.ru", false);
        final User follower1 = new User(2L, "Follower1", "follower1@m.ru", false);
        final User follower2 = new User(3L, "Follower2", "follower2@m.ru", false);
        final Friendship f1 = Friendship.builder().id(2L).friend(user1).follower(follower1).state(PENDING).build();
        final Friendship f2 = Friendship.builder().id(3L).friend(user1).follower(follower2).state(PENDING).build();
        final List<Friendship> friendships = List.of(f1, f2);
        final Friendship aF1 = f1.toBuilder().state(APPROVED).build();
        final Friendship af2 = f2.toBuilder().state(APPROVED).build();
        final List<Friendship> saved = List.of(aF1, af2);

        when(friendshipRepository.findAllById(any())).thenReturn(friendships);
        when(friendshipRepository.saveAll(any())).thenReturn(saved);

        final List<FriendshipShortDto> friendshipShortDtos = service.approveFriendship(1L, List.of(2L, 3L));
        assertEquals(FriendshipMapper.toShortDto(saved), friendshipShortDtos);
    }

    @Test
    void approveFriendship_throwWhenFriendshipListEmpty() {
        when(friendshipRepository.findAllById(any())).thenReturn(Collections.emptyList());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.approveFriendship(1L, List.of(1L, 2L)));
        assertEquals("Request friendship not found.", exception.getMessage());
    }

    @Test
    void approveFriendship_confirmFriendshipRequest() {
        when(friendshipRepository.findAllById(any())).thenReturn(Collections.emptyList());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.approveFriendship(1L, List.of(1L, 2L)));
        assertEquals("Request friendship not found.", exception.getMessage());
    }

    @Test
    void rejectFriendship() {
        final User user1 = new User(1L, "User1", "user1@m.ru", false);
        final User follower1 = new User(2L, "Follower1", "follower1@m.ru", false);
        final User follower2 = new User(3L, "Follower2", "follower2@m.ru", false);
        final Friendship f1 = Friendship.builder().id(2L).friend(user1).follower(follower1).state(PENDING).build();
        final Friendship f2 = Friendship.builder().id(3L).friend(user1).follower(follower2).state(PENDING).build();
        final List<Friendship> friendships = List.of(f1, f2);
        final Friendship aF1 = f1.toBuilder().state(REJECTED).build();
        final Friendship af2 = f2.toBuilder().state(REJECTED).build();
        final List<Friendship> saved = List.of(aF1, af2);

        when(friendshipRepository.findAllById(any())).thenReturn(friendships);
        when(friendshipRepository.saveAll(any())).thenReturn(saved);

        final List<FriendshipShortDto> friendshipShortDtos = service.rejectFriendship(1L, List.of(2L, 3L));
        assertEquals(FriendshipMapper.toShortDto(saved), friendshipShortDtos);
    }

    @Test
    void deleteFriendshipRequest() {
        doNothing().when(userService).checkExistById(anyLong());
        when(friendshipRepository.existsByIdAndFollowerId(anyLong(), anyLong())).thenReturn(true);

        service.deleteFriendshipRequest(1L, 2L);
    }

    @Test
    void deleteFriendshipRequest_throwException() {
        doNothing().when(userService).checkExistById(anyLong());
        when(friendshipRepository.existsByIdAndFollowerId(anyLong(), anyLong())).thenReturn(false);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.deleteFriendshipRequest(1L, 2L));

        assertEquals("Friendship request no exist.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "PENDING", "APPROVED", "REJECTED"})
    void getFriendshipRequests(String filter) {
        final List<Friendship> expected = getExpected(filter);
        final List<FriendshipShortDto> expectedDto = getExpectedByFylter(filter);
        doNothing().when(userService).checkExistById(anyLong());
        when(friendshipRepository.findAll(any(Predicate.class))).thenReturn(expected);

        final List<FriendshipShortDto> actual = service.getFriendshipRequests(1L, filter);
        assertEquals(expectedDto, actual);
    }

    @Test
    void getFriendshipRequests_throwWhenWrongFilter() {
        doNothing().when(userService).checkExistById(anyLong());

        final ConflictException exception = assertThrows(ConflictException.class,
                () -> service.getFriendshipRequests(1L, "filter"));
        assertEquals("Wrong filter. Filter should be one of: [ALL, APPROVED, PENDING, REJECTED]", exception.getMessage());
    }


    @ParameterizedTest
    @ValueSource(strings = {"ALL", "PENDING", "APPROVED", "REJECTED"})
    void getIncomingFriendRequests(String filter) {
        final List<Friendship> expected = getExpected(filter);
        final List<FriendshipShortDto> expectedDto = getExpectedByFylter(filter);
        doNothing().when(userService).checkExistById(anyLong());
        when(friendshipRepository.findAll(any(Predicate.class))).thenReturn(expected);

        final List<FriendshipShortDto> actual = service.getIncomingFriendRequests(1L, filter);
        assertEquals(expectedDto, actual);
    }

    private List<Friendship> getExpected(String filter) {
        switch (filter) {
            case "ALL":
                return all;
            case "PENDING":
                return pending;
            case "APPROVED":
                return approved;
            case "REJECTED":
                return reject;
        }
        return Collections.emptyList();
    }

    private List<FriendshipShortDto> getExpectedByFylter(String filter) {
        switch (filter) {
            case "ALL":
                return allDto;
            case "PENDING":
                return pendingDto;
            case "APPROVED":
                return approvedDto;
            case "REJECTED":
                return rejectDto;
        }
        return Collections.emptyList();
    }
}