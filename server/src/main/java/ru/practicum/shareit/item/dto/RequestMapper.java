package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Request;
import ru.practicum.shareit.user.User;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMapper {

    public RequestDto toDto(Request request) {
        return RequestDto.builder()
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
