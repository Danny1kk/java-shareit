package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service // Делаем бином Spring
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemResponseDto create(Long ownerId, ItemCreateDto dto) {
        User owner = checkUser(ownerId);
        Item item = ItemMapper.mapFromCreateDto(dto);
        item.setOwner(owner);

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto update(Long ownerId, Long itemId, ItemCreateDto newDto) {
        Item item = getItem(itemId);

        if (!isOwner(item, ownerId)) {
            throw new ForbiddenException("Пользователь id " + ownerId + " не является владельцем вещи id " + itemId);
        }

        if (newDto.getName() != null && !newDto.getName().isBlank()) {
            item.setName(newDto.getName());
        }
        if (newDto.getDescription() != null && !newDto.getDescription().isBlank()) {
            item.setDescription(newDto.getDescription());
        }
        if (newDto.getAvailable() != null) {
            item.setAvailable(newDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto findById(Long userId, Long itemId) {
        checkUser(userId);
        return ItemMapper.mapToItemDto(getItem(itemId));
    }

    @Override
    public List<ItemResponseDto> findAllByOwner(Long ownerId) {
        checkUser(ownerId);
        return itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id, Long ownerId) {
        Item item = getItem(id);
        if (!isOwner(item, ownerId)) {
            throw new ForbiddenException("Удалять может только владелец");
        }
        itemRepository.deleteById(id);
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    private boolean isOwner(Item item, Long userId) {
        return item.getOwner().getId().equals(userId);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}