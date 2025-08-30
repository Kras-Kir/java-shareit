package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.RequestDto;
import ru.practicum.shareit.item.dto.RequestInputDto;
import ru.practicum.shareit.item.dto.RequestMapper;
import ru.practicum.shareit.item.model.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final RequestMapper requestMapper;

    @Transactional
    public RequestDto createRequest(Long userId, RequestInputDto requestInputDto) {
        User requester = userService.getUserEntityById(userId);

        Request request = requestMapper.toEntity(requestInputDto, requester);
        Request savedRequest = requestRepository.save(request);

        return requestMapper.toDto(savedRequest);
    }

    public List<RequestDto> getUserRequests(Long userId) {
        userService.getUserById(userId);
        List<Request> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userService.getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<Request> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable);
        return requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RequestDto getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
        return requestMapper.toDto(request);
    }
}