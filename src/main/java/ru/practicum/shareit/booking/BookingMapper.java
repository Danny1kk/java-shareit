package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDtoShort;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponseDto mapToResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStartTime());
        dto.setEnd(booking.getEndTime());
        dto.setStatus(booking.getStatus());

        ItemDto itemDto = ItemMapper.mapToShortDto(booking.getItem());
        dto.setItem(itemDto);

        UserDtoShort userDto = new UserDtoShort();
        userDto.setId(booking.getBooker().getId());
        dto.setBooker(userDto);

        return dto;
    }

    public static BookingShortDto mapToDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }

    public static Booking mapFromCreateDto(
            BookingCreateDto dto,
            Item item,
            User booker
    ) {
        Booking booking = new Booking();

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartTime(dto.getStart());
        booking.setEndTime(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }
}