package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

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

    List<Booking> findAllByItemInAndStatus(List<Item> items, BookingStatus status);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndTimeBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);
}