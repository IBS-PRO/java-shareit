package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.List;

@Component
public interface ItemService {
    ItemDto addItem(long ownerId, ItemDtoRequest itemDtoRequest);

    ItemDto getItem(Long id);

    List<ItemDto> getAllOwnerItems(Long ownerId);

    ItemDto updateItem(Long itemId, ItemDtoRequest itemDtoRequest, Long ownerId);

    List<ItemDto> searchAvailableItem(String text);
}
