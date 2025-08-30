package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    @Query("SELECT r FROM Request r WHERE r.requester.id <> ?1 ORDER BY r.created DESC")
    List<Request> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId, Pageable pageable);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);
}
