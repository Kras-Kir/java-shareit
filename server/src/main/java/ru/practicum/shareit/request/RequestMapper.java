package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Request;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.user.User;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMapper {

    public ItemRequestDto toDto(Request request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(request.getItems() != null ?
                        request.getItems().stream()
                                .map(item -> ItemMapper.toResponseDto(item))
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public Request toEntity(RequestInputDto requestInputDto, User requester) {
        return Request.builder()
                .description(requestInputDto.getDescription())
                .requester(requester)
                .created(java.time.LocalDateTime.now())
                .build();
    }
}