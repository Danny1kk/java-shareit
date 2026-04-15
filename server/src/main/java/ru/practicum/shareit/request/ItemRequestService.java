package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto dto);

    void delete(Long userId, Long requestId);

    List<ItemRequestDto> findAllByUser(Long userId);

    List<ItemRequestDto> findAll(Long userId);

    ItemRequestDto findById(Long userId, Long requestId);
}
