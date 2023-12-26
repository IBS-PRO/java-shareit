package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid RequestDto itemRequestDto) {
        log.info("POST '/requests'. Запрос пользователя с id {} ", userId);
        ResponseEntity<Object> response = requestClient.addItemRequest(itemRequestDto, userId);
        log.info("POST '/requests'. Ответ, запрос: {}, успешно добавлен ", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("GET '/requests/{requestId}'. Запрос на получение запроса пользователя с id {} ", userId);
        ResponseEntity<Object> response = requestClient.getItemRequest(requestId, userId);
        log.info("GET '/requests/{requestId}'. Ответ, запрос: {} ", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getRequestorRequest(@RequestHeader("X-Sharer-User-Id") long requestId) {
        log.info("GET '/requests'. Запрос на получение запросов с id {} ", requestId);
        ResponseEntity<Object> response = requestClient.getRequestorRequest(requestId);
        log.info("GET '/requests'. Ответ, запрос: {} ", response);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Min(0) Integer size) {
        log.info("GET '/requests'. Запрос на получение страницы запросов из {} элементов ", size);
        ResponseEntity<Object> response = requestClient.getItemRequests(userId, from, size);
        log.info("GET '/requests'. Ответ, запрос: {} ", response);
        return response;
    }

}
