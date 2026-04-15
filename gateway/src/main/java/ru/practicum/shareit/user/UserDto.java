package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым или содержать только пробелы")
    private String name;

    @NotBlank(message = "Email не может быть пустым или содержать только пробелы")
    @Email(message = "Email должен быть валидным")
    private String email;
}