package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    List<ItemResponseDto> getAllItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);

    ItemResponseDto getItemByIdWithBookingsAndComments(Long userId, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}