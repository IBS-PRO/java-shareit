package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        Long id = null;
        if (item.getRequest() != null) {
            id = item.getRequest().getId();
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(id)
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
                .owner(owner)
                .build();
    }

    public static Item toItem(ItemDto itemDtoRequest, User owner) {
        if (itemDtoRequest == null || owner == null) {
            throw new IllegalArgumentException("ItemDto или User не может быть null");
        }
        return Item.builder()
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .owner(owner)
                .build();
    }

    public static ItemDtoRequest itemToItemShortForRequestDto(Item item) {
        Long id = null;
        if (item.getRequest() != null) {
            id = item.getRequest().getId();
        }
        return ItemDtoRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(id)
                .build();
    }

    public static List<ItemDto> itemListToItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static List<ItemDtoRequest> itemlistToitemForRequestDtolist(List<Item> items) {
        return items.stream().map(ItemMapper::itemToItemShortForRequestDto).collect(Collectors.toList());
    }

}
