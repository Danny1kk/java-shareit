package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemDto {
    @NotBlank(groups = Create.class)
    private Long id;

    @NotBlank(groups = Create.class)
    private String name;
}