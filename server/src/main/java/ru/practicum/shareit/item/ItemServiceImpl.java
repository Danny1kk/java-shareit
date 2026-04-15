package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponseDto create(Long ownerId, ItemCreateDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.mapFromCreateDto(dto);
        item.setOwner(owner);
        if (dto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(dto.getRequestId()).orElseThrow(
                    () -> new NotFoundException("Запрос не найден"));
            item.setItemRequest(itemRequest);
        }
        return ItemMapper.mapToItemDto(itemRepository.save(item),
                null,
                null,
                List.of());
    }

    @Override
    public ItemResponseDto update(Long ownerId, Long itemId, ItemCreateDto newDto) {
        Item item = getItem(itemId);

        if (!isOwner(item, ownerId))
            throw new ForbiddenException("Редактировать может только владелец");

        Item newItem = ItemMapper.mapFromCreateDto(newDto);

        ItemMapper.updateFields(item, newItem);
        return ItemMapper.mapToItemDto(itemRepository.save(item),
                null,
                null,
                List.of());
    }

    @Override
    public ItemResponseDto findById(Long userId, Long itemId) {
        Item item = getItem(itemId);

        BookingShortDto last = null;
        BookingShortDto next = null;

        if (isOwner(item, userId)) {
            last = getLastBooking(userId, itemId);
            next = getNextBooking(userId, itemId);
        }

        return ItemMapper.mapToItemDto(
                item,
                last,
                next,
                getComments(itemId)
        );
    }

    @Override
    public List<ItemResponseDto> findAllByOwner(Long ownerId) {
        return itemRepository.findAllByOwner_Id(ownerId)
                .stream()
                .map(item -> ItemMapper.mapToItemDto(
                        item,
                        getLastBooking(ownerId, item.getId()),
                        getNextBooking(ownerId, item.getId()),
                        getComments(item.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
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
    public void delete(Long id, Long ownerId) {
        Item item = getItem(id);
        if (!isOwner(item, ownerId)) {
            throw new ForbiddenException("Удалять может только владелец");
        }
        itemRepository.deleteById(id);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = checkUser(userId);
        Item item = getItem(itemId);

        boolean hasBooking = bookingRepository.findByBooker_IdAndEndTimeIsBefore(userId, LocalDateTime.now())
                .stream()
                .anyMatch(booking -> booking.getItem().getId().equals(itemId));
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

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private BookingShortDto getLastBooking(Long ownerId, Long itemId) {
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndEndTimeBefore(ownerId, LocalDateTime.now());
        return bookings.isEmpty() ? null : BookingMapper.mapToDto(bookings.get(0));
    }

    private BookingShortDto getNextBooking(Long ownerId, Long itemId) {
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartTimeAfter(ownerId, LocalDateTime.now());
        return bookings.isEmpty() ? null : BookingMapper.mapToDto(bookings.get(bookings.size() - 1));
    }
}