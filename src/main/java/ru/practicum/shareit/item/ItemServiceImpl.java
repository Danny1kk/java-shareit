package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemResponseDto create(Long ownerId, ItemCreateDto dto) {
        User owner = checkUser(ownerId);
        Item item = ItemMapper.mapFromCreateDto(dto);
        item.setOwner(owner);

        return ItemMapper.mapToItemDto(itemRepository.save(item), null, null, Collections.emptyList());
    }

    @Override
    @Transactional
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

        return ItemMapper.mapToItemDto(
                itemRepository.save(item), null, null, Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto findById(Long userId, Long itemId) {
        Item item = getItem(itemId);

        BookingDto lastBooking = null;
        BookingDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = getLastBooking(itemId);
            nextBooking = getNextBooking(itemId);
        }

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(c -> CommentMapper.mapToDto(c))
                .collect(Collectors.toList());

        return ItemMapper.mapToItemDto(itemRepository.save(item), lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> findAllByOwner(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        return items.stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> ItemMapper.mapToItemDto(
                        item,
                        getLastBooking(item.getId()),
                        getNextBooking(item.getId()),
                        getComments(item.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public void delete(Long id, Long ownerId) {
        Item item = getItem(id);
        if (!isOwner(item, ownerId)) {
            throw new ForbiddenException("Удалять может только владелец");
        }
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = getItem(itemId);

        boolean hasBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasBooking) {
            throw new ValidationException("Вы не можете оставить отзыв");
        }

        Comment comment = CommentMapper.mapToComment(dto, user, item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.mapToDto(commentRepository.save(comment));
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

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToDto)
                .toList();
    }

    private BookingDto getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now())
                .map(b -> BookingMapper.mapToDto(b))
                .orElse(null);
    }

    private BookingDto getNextBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now())
                .map(b -> BookingMapper.mapToDto(b))
                .orElse(null);
    }
}