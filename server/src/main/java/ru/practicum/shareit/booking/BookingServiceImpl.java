package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto create(Long userId, BookingCreateDto dto) {
        User user = getUser(userId);
        Item item = getItem(dto.getItemId());

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь с id=" + item.getId() + " недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Владелец с id=" + userId + " не может забронировать свою вещь"));
        }

        Booking booking = BookingMapper.mapFromCreateDto(dto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.mapToResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId))
            throw new ForbiddenException("Доступ к бронированию запрещен");

        if (booking.getStatus() != BookingStatus.WAITING)
            throw new BadRequestException("Бронирование уже обработано");

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.mapToResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto findById(Long userId, Long bookingId) {
        getUser(userId);
        Booking booking = getBooking(bookingId);
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId))
            throw new NotFoundException("Бронирование отклонено");
        return BookingMapper.mapToResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> findAllByUser(Long userId, String state) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState;

        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неизвестное состояние: " + state);
        }

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBooker_Id(userId);
            case CURRENT -> bookingRepository.findByBooker_IdAndStartTimeIsBeforeAndEndTimeIsAfter(userId, now, now);
            case PAST -> bookingRepository.findByBooker_IdAndEndTimeIsBefore(userId, now);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartTimeIsAfter(userId, now);
            case WAITING -> bookingRepository.findByBooker_Id(userId).stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING).toList();
            case REJECTED -> bookingRepository.findByBooker_Id(userId).stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED).toList();
        };

        bookings.sort(Comparator.comparing(Booking::getStartTime).reversed());
        return bookings.stream().map(BookingMapper::mapToResponseDto).toList();
    }

    @Override
    public List<BookingResponseDto> findAllByOwner(Long ownerId, String state) {
        getUser(ownerId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = parseState(state);


        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItem_Owner_Id(ownerId);
            case CURRENT -> bookingRepository.findByItem_Owner_IdAndStartTimeIsBeforeAndEndTimeIsAfter(ownerId, now, now);
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndTimeBefore(ownerId, now);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartTimeAfter(ownerId, now);
            case WAITING -> bookingRepository.findByItem_Owner_Id(ownerId).stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING).toList();
            case REJECTED -> bookingRepository.findByItem_Owner_Id(ownerId).stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED).toList();
        };

        return bookings.stream().map(BookingMapper::mapToResponseDto).toList();
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Неизвестное состояние: " + state);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=" + userId + " не найден")));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id=" + itemId + " не найдена")));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=" + bookingId + " не найдено")));
    }
}