package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @Validated(Create.class)
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                           @RequestBody @Valid ItemDto itemDtoRequest) {
        log.info("POST '/items'. Запрос на добавление вещи с телом {} ", itemDtoRequest);
        ResponseEntity<Object> response = itemClient.addItem(ownerId, itemDtoRequest);
        log.info("POST '/items'. Ответ, новая вещь, успешно добавлена ");
        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("GET '/items/{id}'. Запрос на получение вещи с id: {}", itemId);
        ResponseEntity<Object> response = itemClient.getItem(itemId, ownerId);
        log.info("GET '/items/{id}'. Ответ, вещь c id: {}, {} ", itemId, response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) Integer size) {
        log.info("GET '/items'. Запрос на получение вещи владельца с id: {}", ownerId);
        ResponseEntity<Object> response = itemClient.getAllOwnerItems(ownerId, from, size);
        log.info("GET '/items'. Ответ, все вещи пользователя с id: {}, {} ", ownerId, response);
        return response;
    }

    @PatchMapping("/{itemId}")
    @Validated(Update.class)
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestBody ItemDto itemDtoRequest) {
        log.info("PATCH '/items'. Запрос на обновление вещи с id {} пользователя c id {} ", itemId, ownerId);
        ResponseEntity<Object> response = itemClient.updateItem(itemId, itemDtoRequest, ownerId);
        log.info("PATCH '/users/{itemId}'. Ответ, вещь с id {} обновлен: {} ", itemId, response);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text,
                                              @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) Integer size)  {
        log.info("PATCH '/items/search'. Запрос на поиск вещи по тексту {} ", text);
        ResponseEntity<Object> response = itemClient.searchAvailableItem(userId, text, from, size);
        log.info("PATCH '/items/search'. Ответ, по запросу найдено: {} ", response);
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid CommentDto commentDto) {
        log.info("POST 'item/{itemId}/comment'. Запрос на добавление комментария с телом {} ", commentDto);
        ResponseEntity<Object> response = itemClient.addComment(itemId, commentDto, userId);
        log.info("POST 'item/{itemId}/comment'. Ответ, новый комментарий {}, успешно добавлен ", response);
        return response;
    }

}
