package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    public ItemResponseDto findById(Long userId, Long itemId) {
        Item item = getItem(itemId);
        LocalDateTime now = LocalDateTime.now();

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED);

            lastBooking = bookings.stream()
                    .filter(b -> !b.getStartTime().isAfter(now))
                    .max(Comparator.comparing(Booking::getStartTime))
                    .map(BookingMapper::mapToDto)
                    .orElse(null);

            nextBooking = bookings.stream()
                    .filter(b -> b.getStartTime().isAfter(now))
                    .min(Comparator.comparing(Booking::getStartTime))
                    .map(BookingMapper::mapToDto)
                    .orElse(null);
        }

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapToDto)
                .toList();

        return ItemMapper.mapToItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemResponseDto> findAllByOwner(Long ownerId) {
        checkUser(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Item> items = itemRepository.findAllByOwnerId(ownerId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .toList();

        List<Booking> allBookings = bookingRepository.findAllByItemInAndStatus(items, BookingStatus.APPROVED);
        Map<Long, List<Booking>> bookingsByItem = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        List<Comment> allComments = commentRepository.findAllByItemIn(items);
        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());

                    BookingShortDto last = itemBookings.stream()
                            .filter(b -> !b.getStartTime().isAfter(now))
                            .max(Comparator.comparing(Booking::getStartTime))
                            .map(BookingMapper::mapToDto)
                            .orElse(null);

                    BookingShortDto next = itemBookings.stream()
                            .filter(b -> b.getStartTime().isAfter(now))
                            .min(Comparator.comparing(Booking::getStartTime))
                            .map(BookingMapper::mapToDto)
                            .orElse(null);

                    List<CommentDto> comments = commentsByItem.getOrDefault(item.getId(), List.of()).stream()
                            .map(CommentMapper::mapToDto)
                            .toList();

                    return ItemMapper.mapToItemDto(item, last, next, comments);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text)
                .stream()
                .map(item -> ItemMapper.mapToItemDto(item, null, null, Collections.emptyList()))
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
        User user = checkUser(userId);
        Item item = getItem(itemId);

        boolean hasBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndTimeBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasBooking) {
            throw new BadRequestException("Вы не можете оставить отзыв на вещь с id=" + itemId);
        }

        Comment comment = CommentMapper.mapToComment(dto, user, item);
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
}