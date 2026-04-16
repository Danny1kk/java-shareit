package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    // находим автора запроса вещи
    List<ItemRequest> findByRequester_IdOrderByCreateTimeDesc(Long userId);

    List<ItemRequest> findAllByRequester_IdNotOrderByCreateTimeDesc(Long userId);
}