package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto create(Long ownerId, ItemCreateDto dto);

    ItemResponseDto update(Long ownerId, Long itemId, ItemCreateDto newDto);

    void delete(Long id, Long userId);

    ItemResponseDto findById(Long userId, Long itemId);

    List<ItemResponseDto> findAllByOwner(Long ownerId);

    List<ItemResponseDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto dto);
}