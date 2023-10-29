package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Component
public interface ItemService {
    ItemDto addItem(long ownerId, ItemDto itemDto);

    ItemDto getItem(Long id);

    List<ItemDto> getAllOwnerItems(Long ownerId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId);

    List<ItemDto> searchAvailableItem(String text);
}
