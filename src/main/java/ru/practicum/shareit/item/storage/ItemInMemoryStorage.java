package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemInMemoryStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item addItem(Item item) {
        item.setId(idGenerator());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllByOwner(User owner) {
        return items.values().stream()
                .filter(i -> i.getOwner()
                        .getId()
                        .equals(owner.getId()))
                .collect(Collectors.toList());

    }

    @Override
    public List<Item> searchAvailableItem(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        items.put(itemId, item);
        return items.get(itemId);
    }

    private Long idGenerator() {
        return ++idCounter;
    }

}
