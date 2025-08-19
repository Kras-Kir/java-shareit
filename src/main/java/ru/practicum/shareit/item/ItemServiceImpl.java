package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.error.ForbiddenException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userService.getUserEntityById(userId);
        Item item = ItemMapper.toItem(itemDto, owner); // Теперь передаем владельца
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Редактирование запрещено: пользователь не является владельцем");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName().trim());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription().trim());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(existingItem);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        if (!userService.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        return itemRepository.findByOwnerIdOrderById(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableItems(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getItemByIdWithBookingsAndComments(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        // Получаем комментарии
        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, comments);
        itemResponseDto.setComments(comments);

        LocalDateTime now = LocalDateTime.now();

        // Заполняем информацию о бронированиях, если пользователь - владелец
        if (item.getOwner().getId().equals(userId)) {
            // Последнее завершенное бронирование
            bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                            item.getId(), BookingStatus.APPROVED, now)
                    .ifPresent(booking -> itemResponseDto.setLastBooking(ItemMapper.toBookingShortDto(booking)));

            // Ближайшее будущее бронирование
            bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), BookingStatus.APPROVED, now)
                    .ifPresent(booking -> itemResponseDto.setNextBooking(ItemMapper.toBookingShortDto(booking)));
        }

        return itemResponseDto;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        // 1. Проверяем существование пользователя
        User author = userService.getUserEntityById(userId);

        // 2. Проверяем существование вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        // 3. Проверяем, что пользователь брал вещь в аренду и бронирование завершено
        if (!commentRepository.existsApprovedBookingForUser(itemId, userId, LocalDateTime.now())) {
            throw new ValidationException("Пользователь не может оставить комментарий к вещи, " +
                    "которую не арендовал или бронирование еще не завершено");
        }

        // 4. Проверяем, что текст комментария не пустой
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        // 5. Создаем и сохраняем комментарий
        Comment comment = ItemMapper.toComment(commentDto, item, author);
        Comment savedComment = commentRepository.save(comment);

        // 6. Возвращаем DTO
        return ItemMapper.toCommentDto(savedComment);
    }

}
