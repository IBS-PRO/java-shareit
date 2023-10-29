package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto addItem(long ownerId, ItemDto itemDto) {
        User usr = UserMapper.toUser(userService.getUser(ownerId));
        Item item = ItemMapper.toItem(itemDto, usr);
        if (item.getAvailable() == null) {
            throw new ValidationException("Ошибка вещь недоступна");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("Ошибка пустое описание");
        }
        if (item.getName() == null || item.getName().equals("")) {
            throw new ValidationException("Ошибка некорректное имя");
        }
        log.info("Создан item пользователем с id: {}", usr.getId());
        return ItemMapper.toItemDto(itemStorage.addItem(item, usr));
    }

    @Override
    public ItemDto getItem(Long id) {
        if (itemStorage.getItem(id) == null) {
            throw new NotFoundException("Вещь с id: " + id + " не найдена");
        }
        log.info("Получен item с id: {}", id);
        return ItemMapper.toItemDto(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getAllOwnerItems(Long ownerId) {
        User usr = UserMapper.toUser(userService.getUser(ownerId));
        log.info("Получены все items пользователя с id: {}", ownerId);
        return itemStorage.getAllByOwner(usr).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item oldItem = itemStorage.getItem(itemId);
        User owner = UserMapper.toUser(userService.getUser(ownerId));
        Item item = ItemMapper.toItem(itemDto, owner);
        if (item.getOwner() == null) {
            item.setOwner(owner);
        }
        if (!oldItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Некорректный пользователь");
        }
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        item.setId(itemId);
        log.info("Обновлен item с id: {}", itemId);
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, item, owner));

    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        log.info("Найдены все items по запросу '{}'", text);
        return itemStorage.searchAvailableItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
