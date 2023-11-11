package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item getItem(long id);

    List<Item> getAllByOwner(User owner);

    List<Item> searchAvailableItem(String text);

    Item updateItem(Long itemId, Item item);
}
