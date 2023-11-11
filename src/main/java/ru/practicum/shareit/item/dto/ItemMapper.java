package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest, User owner) {
        if (itemDtoRequest == null || owner == null) {
            throw new IllegalArgumentException("ItemDto или User не может быть null");
        }
        return Item.builder()
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .request(itemDtoRequest.getRequest())
                .owner(owner)
                .build();
    }

}
