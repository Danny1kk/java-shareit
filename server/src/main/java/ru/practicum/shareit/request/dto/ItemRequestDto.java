package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    @JsonProperty("created")
    private LocalDateTime createTime;

    private List<ItemResponseDto> items;
}