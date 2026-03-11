package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(Long ownerId, ItemCreateDto dto);

    ItemResponseDto update(Long ownerId, Long itemId, ItemCreateDto newDto);

    void delete(Long id, Long userId);

    ItemResponseDto findById(Long userId, Long itemId);

    List<ItemResponseDto> findAllByOwner(Long ownerId);

    List<ItemResponseDto> search(String text);
}