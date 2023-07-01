package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.subs.FriendshipDto;
import ru.practicum.dto.subs.FriendshipShortDto;
import ru.practicum.model.Friendship;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendshipMapper {
    public static FriendshipDto toDto(Friendship friendship) {
        return FriendshipDto.builder()
                .id(friendship.getId())
                .followerId(friendship.getFollower().getId())
                .friend(UserMapper.toShotDto(friendship.getFriend()))
                .state(friendship.getState())
                .build();
    }

    public static FriendshipShortDto toShortDto(Friendship friendship) {
        return FriendshipShortDto.builder()
                .id(friendship.getId())
                .friend(UserMapper.toShotDto(friendship.getFriend()))
                .state(friendship.getState())
                .build();
    }

    public static List<FriendshipShortDto> toShortDto(List<Friendship> subs) {
        return subs.stream()
                .map(FriendshipMapper::toShortDto)
                .collect(Collectors.toList());
    }
}
