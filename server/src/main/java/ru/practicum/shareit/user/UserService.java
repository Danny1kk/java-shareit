package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto create(UserDto dto);

    UserDto update(Long userId, UserDto newDto);

    void delete(Long id);

    List<UserDto> findAll();

    UserDto findById(Long id);
}