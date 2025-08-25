package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.RequestDto;
import ru.practicum.shareit.item.dto.RequestInputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;


    @PostMapping
    public ResponseEntity<RequestDto> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody RequestInputDto requestInputDto) {
        return ResponseEntity.ok(requestService.createRequest(userId, requestInputDto));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.getUserRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return ResponseEntity.ok(requestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.getRequestById(userId, requestId));
    }
}
