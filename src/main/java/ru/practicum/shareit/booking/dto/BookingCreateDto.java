package ru.practicum.shareit.booking.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    @NotNull
    private Long itemId;

    @NotNull
    @Future
    @Column(name = "start_date")
    private LocalDateTime start;

    @NotNull
    @Future
    @Column(name = "end_date")
    private LocalDateTime end;
}