package ru.practicum.dto.subs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.FriendshipState;

@Builder
@Data
public class FriendshipDto {
    private Long id;
    private Long followerId;
    private UserShortDto friend;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private FriendshipState state;
}
