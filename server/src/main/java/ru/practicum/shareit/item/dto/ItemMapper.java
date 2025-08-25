package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    /*public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }*/
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        // ДОБАВЬТЕ ЭТУ ЛОГИКУ:
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);  // Устанавливаем владельца
        if (itemDto.getRequestId() != null) {
            Request request = new Request();
            request.setId(itemDto.getRequestId());
            item.setRequest(request);
        }  // Устанавливаем requestId напрямую
        return item;
    }


    public static ItemResponseDto toItemResponseDto(Item item, List<CommentDto> comments) {
        ItemResponseDto dto = toResponseDto(item);
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        dto.setComments(comments != null ? comments : new ArrayList<>()); // гарантируем не-null список
        dto.setLastBooking(null); // явно инициализируем
        dto.setNextBooking(null); // явно инициализируем
        return dto;
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static ItemResponseDto toResponseDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }

        dto.setComments(new ArrayList<>()); // пустой список комментариев
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        return dto;
    }
}






