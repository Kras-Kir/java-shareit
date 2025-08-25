package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;  // Последнее завершенное бронирование
    private BookingShortDto nextBooking;  // Ближайшее активное бронирование
    private Long requestId;              // ID запроса, если вещь создана по запросу
    private List<CommentDto> comments;
}