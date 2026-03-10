package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter @Setter
public class ItemDto {
    private Long id;
    private String url;
    private Set<String> tags;
}