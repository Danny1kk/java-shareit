package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Headers;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto create(@RequestHeader(Headers.USER_ID) Long ownerId,
                                  @Valid @RequestBody ItemCreateDto dto) {
        return itemService.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader(Headers.USER_ID) Long ownerId,
                                  @PathVariable Long itemId,
                                  @RequestBody ItemCreateDto newDto) {
        return itemService.update(ownerId, itemId, newDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable(name = "itemId") Long id,
                       @RequestHeader(Headers.USER_ID) Long ownerId) {
        itemService.delete(id, ownerId);
    }

    @GetMapping
    public List<ItemResponseDto> findAllByOwner(@RequestHeader(Headers.USER_ID) Long ownerId) {
        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findById(@RequestHeader(Headers.USER_ID) Long userId,
                                    @PathVariable(name = "itemId") Long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(Headers.USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto dto) {
        return itemService.addComment(userId, itemId, dto);
    }
}