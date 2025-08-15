package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ForbiddenException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 1L;
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }


    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        UserDto userDto = userService.getUserById(userId);
        User owner = UserMapper.toUser(userDto);

        Item item = ItemMapper.toItem(itemDto);
        item.setId(idCounter++);
        item.setOwner(owner);
        items.put(item.getId(), item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = items.get(itemId);
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь " + userId + " не является владельцем предмета");
        }

        if (itemDto.getName() != null && !itemDto.getName().trim().isEmpty()) {
            existingItem.setName(itemDto.getName().trim());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().trim().isEmpty()) {
            existingItem.setDescription(itemDto.getDescription().trim());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String lowerCaseText = text.toLowerCase();

        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(lowerCaseText) ||
                                item.getDescription().toLowerCase().contains(lowerCaseText)))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}