package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым или содержать только пробелы")
    private String name;

    @NotBlank(message = "Описание не может быть пустым или содержать только пробелы")
    private String description;

    @NotNull(message = "Доступность не может не быть назначенной")
    private Boolean available;

    private Long requestId;
}