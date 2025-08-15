package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);
}