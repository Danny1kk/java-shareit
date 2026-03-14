package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByBooker_IdAndEndIsBefore(
            Long bookerId, LocalDateTime time
    );

    List<Booking> findByBooker_IdAndStartIsAfter(
            Long bookerId, LocalDateTime time
    );

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findByItem_Owner_Id(Long ownerId);

    List<Booking> findByItem_Owner_IdAndEndBefore(
            Long ownerId, LocalDateTime time
    );

    List<Booking> findByItem_Owner_IdAndStartAfter(
            Long ownerId, LocalDateTime time
    );

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
            Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);
}