package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingCreateDto dto);

    BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingResponseDto findById(Long userId, Long bookingId);

    List<BookingResponseDto> findAllByUser(Long userId, String state);

    List<BookingResponseDto> findAllByOwner(Long ownerId, String state);
}