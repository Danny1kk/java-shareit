package ru.practicum.shareit.booking.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    @NotNull
    private Long itemId;

    @NotNull
    @FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime startTime;

    @NotNull
    @FutureOrPresent
    @Column(name = "end_date")
    private LocalDateTime endTime;
}