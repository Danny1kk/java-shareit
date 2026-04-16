package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Headers;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(Headers.USER_ID) Long userId,
                                                @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Создание запроса вещи {}, userId={}", requestDto, userId);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(Headers.USER_ID) Long userId) {
        log.info("Получение своих запросов вещей, userId={}", userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(Headers.USER_ID) Long userId) {
        log.info("Получение всех запросов вещей, userId={}", userId);
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(Headers.USER_ID) Long userId,
                                             @PathVariable long requestId) {
        log.info("Получение запроса вещи requestId={}, userId={}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }
}