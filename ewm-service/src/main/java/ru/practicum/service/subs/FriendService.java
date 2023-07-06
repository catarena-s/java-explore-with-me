package ru.practicum.service.subs;

import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface FriendService {
    List<EventShortDto> getParticipateEvents(long followerId, int from, int size);

    List<UserDto> getFriends(long followerId);

    List<EventShortDto> getFriendEvents(long followerId, int from, int size);

    List<UserDto> getFollowers(long userId);
}
