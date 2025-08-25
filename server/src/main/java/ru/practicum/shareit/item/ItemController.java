package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemByIdWithBookingsAndComments(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getAllItemsByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam String text) {
        return ResponseEntity.ok(itemService.searchItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto));
    }
}
