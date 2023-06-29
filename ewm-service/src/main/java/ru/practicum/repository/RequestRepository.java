package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByIdAndRequesterId(long requestId, long userId);

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByIdInAndStatus(List<Long> requestIds, RequestStatus requestStatus);

    List<Request> findAllByEvent_InitiatorIdAndEventId(long userId, long eventId);

    boolean existsByEventIdAndRequesterId(long eventId, long userId);

    boolean existsByRequesterId(long userId);
}
