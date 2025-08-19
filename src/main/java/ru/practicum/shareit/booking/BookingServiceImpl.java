package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.error.ForbiddenException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.booking.BookingState;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingState.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.toEntity(bookingDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.toDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Доступ запрещен");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        PageRequest pageRequest = createPageRequest(from, size);
        LocalDateTime now = LocalDateTime.now();

        BookingState bookingState = validateState(state);
        Page<Booking> bookingsPage;

        switch (bookingState) {
            case CURRENT:
                bookingsPage = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                        userId, now, now, pageRequest);
                break;
            case PAST:
                bookingsPage = bookingRepository.findByBookerIdAndEndBefore(
                        userId, now, pageRequest);
                break;
            case FUTURE:
                bookingsPage = bookingRepository.findByBookerIdAndStartAfter(
                        userId, now, pageRequest);
                break;
            case WAITING:
                bookingsPage = bookingRepository.findByBookerIdAndStatus(
                        userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookingsPage = bookingRepository.findByBookerIdAndStatus(
                        userId, BookingStatus.REJECTED, pageRequest);
                break;
            default: // ALL
                bookingsPage = bookingRepository.findByBookerId(userId, pageRequest);
        }

        return bookingsPage.getContent()
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, int from, int size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        PageRequest pageRequest = createPageRequest(from, size);
        LocalDateTime now = LocalDateTime.now();

        Page<Booking> bookingsPage;

        switch (validateState(state)) {
            case CURRENT:
                bookingsPage = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                        ownerId, now, now, pageRequest);
                break;
            case PAST:
                bookingsPage = bookingRepository.findByItemOwnerIdAndEndBefore(
                        ownerId, now, pageRequest);
                break;
            case FUTURE:
                bookingsPage = bookingRepository.findByItemOwnerIdAndStartAfter(
                        ownerId, now, pageRequest);
                break;
            case WAITING:
            case REJECTED:
                bookingsPage = bookingRepository.findByItemOwnerIdAndStatus(
                        ownerId, BookingStatus.valueOf(state), pageRequest);
                break;
            default: // ALL
                bookingsPage = bookingRepository.findByItemOwnerId(ownerId, pageRequest);
        }

        return bookingsPage.getContent()
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private PageRequest createPageRequest(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }
        return PageRequest.of(from / size, size, Sort.by("start").descending());
    }

    private BookingState validateState(String state) {
        try {
            return BookingState.from(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}