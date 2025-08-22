package ru.practicum.shareit.item.comment;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :authorId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now")
    boolean existsApprovedBookingForUser(@Param("itemId") Long itemId,
                                         @Param("authorId") Long authorId,
                                         @Param("now") LocalDateTime now);

    List<Comment> findByItemIdInOrderByCreatedDesc(List<Long> itemIds);
}