package ru.practicum.dto.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.FriendshipState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipShortDto {
    private long id;
    private FriendshipState state;
    private UserShortDto friend;
}
