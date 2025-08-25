package ru.practicum.shareit.booking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    @NotNull(message = "Дата начала не может быть нулевой")
    @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
    private LocalDateTime start;

    @NotNull(message = "Конечная дата не может быть нулевой")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "Идентификатор элемента не может быть нулевым")
    @Positive(message = "Идентификатор товара должен быть положительным")
    private Long itemId;

    @AssertTrue(message = "Дата окончания должна быть после даты начала")
    private boolean isEndAfterStart() {
        return end == null || start == null || end.isAfter(start);
    }
}