package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("POST '/items'. Запрос на добавление вещи с телом {} ", itemDtoRequest);
        ItemDto response = itemService.addItem(ownerId, itemDtoRequest);
        log.info("POST '/items'. Ответ, новая вещь: {}, успешно добавлена ", response);
        return response;
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable long id) {
        log.info("GET '/items/{id}'. Запрос на получение вещи с id: {}", id);
        ItemDto response = itemService.getItem(id);
        log.info("GET '/items/{id}'. Ответ, вещь c id: {}, {} ", id, response);
        return response;
    }

    @GetMapping
    public List<ItemDto> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("GET '/items'. Запрос на получение вещи владельца с id: {}", ownerId);
        List<ItemDto> response = itemService.getAllOwnerItems(ownerId);
        log.info("GET '/items'. Ответ, все вещи пользователя с id: {}, {} ", ownerId, response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId,
                              @RequestBody @Valid ItemDtoRequest itemDtoRequest) {
        log.info("PATCH '/items'. Запрос на обновление вещи с id {} пользователя c id {} ", itemId, ownerId);
        ItemDto response = itemService.updateItem(itemId, itemDtoRequest, ownerId);
        log.info("PATCH '/users/{itemId}'. Ответ, вещь с id {} обновлен: {} ", itemId, response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItem(@RequestParam(name = "text") String text) {
        log.info("PATCH '/items/search'. Запрос на поиск вещи по тексту {} ", text);
        List<ItemDto> response = itemService.searchAvailableItem(text);
        log.info("PATCH '/items/search'. Ответ, по запросу найдено: {} ", response);
        return response;
    }

}
