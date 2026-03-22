package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByBooker_IdAndEndTimeIsBefore(
            Long bookerId, LocalDateTime time
    );

    List<Booking> findByBooker_IdAndStartTimeIsAfter(
            Long bookerId, LocalDateTime time
    );

    List<Booking> findByBooker_IdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findByItem_Owner_Id(Long ownerId);

    List<Booking> findByItem_Owner_IdAndEndTimeBefore(
            Long ownerId, LocalDateTime time
    );

    List<Booking> findByItem_Owner_IdAndStartTimeAfter(
            Long ownerId, LocalDateTime time
    );

    List<Booking> findByItem_Owner_IdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartTimeBeforeOrderByEndTimeDesc(
            Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
            Long itemId, BookingStatus status, LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndStatusAndEndTimeBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);
}