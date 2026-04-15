package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Headers;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(Headers.USER_ID) Long userId,
                                 @RequestBody ItemRequestCreateDto dto) {
        return itemRequestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUser(@RequestHeader(Headers.USER_ID) Long userId) {
        return itemRequestService.findAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(Headers.USER_ID) Long userId) {
        return itemRequestService.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(Headers.USER_ID) Long userId,
                                   @PathVariable Long requestId) {
        return itemRequestService.findById(userId, requestId);
    }

    @DeleteMapping("/{requestId}")
    public void delete(@RequestHeader(Headers.USER_ID) Long userId,
                       @PathVariable Long requestId) {
        itemRequestService.delete(userId, requestId);
    }
}
