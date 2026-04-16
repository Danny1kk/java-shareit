package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        User requester = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("Пользователь с id=" + userId + " не найден")));
        ItemRequest itemRequest = ItemRequestMapper.mapFromCreateDto(dto, requester);
        return ItemRequestMapper.mapToDto(itemRequestRepository.save(itemRequest), List.of());
    }

    @Override
    public void delete(Long userId, Long requestId) {
        if (!isRequestor(getRequest(requestId), userId))
            throw new ForbiddenException("Удалять может только владелец");
        itemRequestRepository.deleteById(requestId);
    }

    @Override
    public List<ItemRequestDto> findAllByUser(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findByRequester_IdOrderByCreateTimeDesc(userId);
        return buildRequestDtos(requests);
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdNotOrderByCreateTimeDesc(userId);
        return buildRequestDtos(requests);
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        List<Item> items = itemRepository.findAllByItemRequest_Id(requestId);

        List<ItemResponseDto> itemDtos = items.stream()
                .map(item -> ItemMapper.mapToItemDto(item, null, null, List.of()))
                .toList();

        return ItemRequestMapper.mapToDto(getRequest(requestId), itemDtos);
    }

    private ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id=" + requestId + " не найден")));
    }

    private boolean isRequestor(ItemRequest request, Long userId) {
        return request.getRequester().getId().equals(userId);
    }

    private List<ItemRequestDto> buildRequestDtos(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findAllByItemRequest_IdIn(requestIds);

        Map<Long, List<ItemResponseDto>> itemsByRequest = allItems.stream()
                .filter(item -> item.getItemRequest() != null)
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId(),
                        Collectors.mapping(ItemMapper::mapToItemDto, Collectors.toList())));

        return requests.stream()
                .map(request -> {
                    List<ItemResponseDto> items = itemsByRequest.getOrDefault(request.getId(), Collections.emptyList());
                    return ItemRequestMapper.mapToDto(request, items);
                })
                .collect(Collectors.toList());
    }
}