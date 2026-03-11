package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto create(UserDto dto) {
        if (repository.existsByEmail(dto.getEmail()))
            throw new ConflictException("Этот email уже используется");

        User user = UserMapper.mapToUser(dto);
        return UserMapper.mapToUserDto(repository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDto newDto) {
        User user = findUserById(userId);

        if (newDto.getEmail() != null && !newDto.getEmail().equals(user.getEmail())) {
            if (repository.existsByEmail(newDto.getEmail())) {
                throw new ConflictException("Email уже используется другим пользователем");
            }
        }

        User newUser = UserMapper.mapToUser(newDto);
        UserMapper.updateFields(user, newUser);
        return UserMapper.mapToUserDto(repository.save(user));
    }

    @Override
    public void delete(Long id) {
        repository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

        repository.deleteById(id);
    }

    @Override
    public List<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.mapToUserDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    private User findUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}