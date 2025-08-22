package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingRequestDto {
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    @NotNull
    @Positive
    private Long itemId;

    @AssertTrue(message = "End date must be after start date")
    private boolean isEndAfterStart() {
        return end == null || start == null || end.isAfter(start);
    }
}
