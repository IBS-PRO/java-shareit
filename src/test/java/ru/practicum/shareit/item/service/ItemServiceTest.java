package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void searchAvailableItem_isValid() {
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("testDescription");
        item.setAvailable(true);
        item.setOwner(new User());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Item> returnedList = new ArrayList<>();
        returnedList.add(item);
        when(itemRepository.getItemsByQuery("testItem")).thenReturn(returnedList);

        List<ItemDto> actual = itemService.searchAvailableItem("testItem");
        assertEquals(itemDto.getId(), actual.get(0).getId());
        assertEquals(itemDto.getName(), actual.get(0).getName());
        assertEquals(itemDto.getDescription(), actual.get(0).getDescription());
    }

    @Test
    void searchAvailableItem_isBlank() {
        assertTrue(itemService.searchAvailableItem("").isEmpty());
    }

}
