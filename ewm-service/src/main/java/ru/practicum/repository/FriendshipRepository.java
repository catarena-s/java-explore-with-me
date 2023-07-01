package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.FriendshipState;
import ru.practicum.model.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long>, QuerydslPredicateExecutor<Friendship> {
    void deleteByFollowerIdAndId(long followerId, long friendId);

    boolean existsByFollowerIdAndFriendIdAndStateNot(long followerId, long friendId, FriendshipState status);

    boolean existsByIdAndFollowerId(long subsId, long followerId);
}
