package ru.practicum.service.subs;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.Event;
import ru.practicum.model.QUser;
import ru.practicum.model.User;
import ru.practicum.service.user.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.enums.FriendshipState.APPROVED;
import static ru.practicum.model.QEvent.event;
import static ru.practicum.model.QFriendship.friendship;
import static ru.practicum.model.QRequest.request;

@Service

public class FriendServiceImpl implements FriendService {
    private final UserService userService;
    private final JPAQueryFactory queryFactory;

    public FriendServiceImpl(UserService userService,
                             EntityManager em) {
        this.userService = userService;
        this.queryFactory = new JPAQueryFactory(em);
    }

    /** Получение списка друзей */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFriends(long userId) {
        userService.checkExistById(userId);
        final List<User> fiends = getUserList(friendship.friend, friendship.follower, userId);
        return UserMapper.toDto(fiends);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long userId) {
        userService.checkExistById(userId);
        final List<User> fiends = getUserList(friendship.follower, friendship.friend, userId);
        return UserMapper.toDto(fiends);
    }

    /** Получить список событий в которых примут участие друзья */
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getParticipateEvents(long followerId, int from, int size) {
        userService.checkExistById(followerId);
        final List<Event> events =
                queryFactory
                        .selectDistinct(request.event)
                        .from(request)
                        .innerJoin(friendship).on(friendship.friend.eq(request.requester))
                        .where(friendship.follower.id.eq(followerId)
                                .and(request.isPrivate.isFalse())
                                .and(request.status.eq(RequestStatus.CONFIRMED))
                                .and(request.event.eventDate.after(LocalDateTime.now().plusHours(2))))
                        .offset(from)
                        .limit(size)
                        .fetch();


        return EventMapper.toDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getFriendEvents(long followerId, int from, int size) {
        userService.checkExistById(followerId);
        final List<Event> events =
                queryFactory
                        .selectDistinct(event)
                        .from(event)
                        .innerJoin(friendship).on(friendship.friend.eq(event.initiator))
                        .where(friendship.follower.id.eq(followerId)
                                .and(event.state.eq(EventState.PUBLISHED))
                                .and(event.eventDate.after(LocalDateTime.now().plusHours(2))))
                        .offset(from)
                        .limit(size)
                        .fetch();

        return EventMapper.toDto(events);
    }

    private List<User> getUserList(QUser friend, QUser user, long userId) {
        return queryFactory
                .select(friend)
                .from(friendship)
                .where(user.id.eq(userId).and(friendship.state.eq(APPROVED)))
                .fetch();
    }
}
