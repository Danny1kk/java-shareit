package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest mapFromCreateDto(
            ItemRequestCreateDto dto,
            User requester
    ) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setCreateTime(LocalDateTime.now());
        request.setRequester(requester);

        return request;
    }

    public static ItemRequestDto mapToDto(
            ItemRequest request,
            List<ItemResponseDto> items
    ) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setCreateTime(request.getCreateTime());
        dto.setDescription(request.getDescription());
        dto.setItems(items);

        return dto;
    }
}