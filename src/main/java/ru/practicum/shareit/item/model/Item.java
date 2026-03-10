package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class Item {
    private Long id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest itemRequest;
}