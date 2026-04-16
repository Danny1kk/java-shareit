package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public static User mapToUser(UserDto dto) {
        User user = new User();

        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    public static void updateFields(User user, User newUser) {
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            user.setName(newUser.getName());
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank() && newUser.getEmail().contains("@")) {
            user.setEmail(newUser.getEmail());
        }
    }
}