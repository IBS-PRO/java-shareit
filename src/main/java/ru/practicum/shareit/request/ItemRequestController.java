package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemResponseDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("POST '/requests'. Запрос пользователя с id {} ", userId);
        ItemResponseDto response = itemRequestService.addItemRequest(itemRequestDto, userId);
        log.info("POST '/requests'. Ответ, запрос: {}, успешно добавлен ", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ItemResponseDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        log.info("GET '/requests/{requestId}'. Запрос на получение запроса пользователя с id {} ", userId);
        ItemResponseDto response = itemRequestService.getItemRequest(requestId, userId);
        log.info("GET '/requests/{requestId}'. Ответ, запрос: {} ", response);
        return response;
    }

    @GetMapping
    public List<ItemResponseDto> getRequestorRequest(@RequestHeader("X-Sharer-User-Id") long requestId) {
        log.info("GET '/requests'. Запрос на получение запросов с id {} ", requestId);
        List<ItemResponseDto> response = itemRequestService.getRequestorRequest(requestId);
        log.info("GET '/requests'. Ответ, запрос: {} ", response);
        return response;
    }

    @GetMapping("/all")
    public List<ItemResponseDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Min(0) Integer size) {
        log.info("GET '/requests'. Запрос на получение страницы запросов из {} элементов ", size);
        List<ItemResponseDto> response = itemRequestService.getItemRequests(userId, from, size);
        log.info("GET '/requests'. Ответ, запрос: {} ", response);
        return response;
    }

    @DeleteMapping("/{requestId}")
    public void deleteItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET '/requests/{requestId}'. Запрос на удаление запроса с id {} ", requestId);
        itemRequestService.deleteItemRequest(requestId, userId);
    }
}
