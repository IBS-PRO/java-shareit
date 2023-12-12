package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Test
    void getAllOwnerItems() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("testUser@email.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("testItem")
                .description("testItemDescription")
                .available(true)
                .owner(user)
                .build();

        System.out.println("Это Юзер " + user);
        System.out.println("Это Итем " + item);

        ArrayList<Item> returned = new ArrayList<>();
        returned.add(item);

        System.out.println("Это массив " + returned);

        when(userRepository.checkUser(user.getId())).thenReturn(user);
        when(itemRepository.getByOwnerId(user.getId())).thenReturn(returned);
        when(bookingRepository.getAllByEndDateBeforeAndStatusAndSubjectInOrderByStartDateDesc(
                any(), any(), any())).thenReturn(new ArrayList<>());

        List<ItemDto> actual = itemService.getAllOwnerItems(user.getId());
        assertEquals(actual.get(0).getId(), item.getId());

        verify(itemRepository).getByOwnerId(item.getId());
    }

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

    @Test
    void addItem() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("testUser@email.com")
                .build();

        BookingDtoResponseShort bookingDtoResponseShort = BookingDtoResponseShort.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(0L)
                .name("testItem")
                .description("descriptionForTestItem")
                .available(true)
                .lastBooking(bookingDtoResponseShort)
                .nextBooking(bookingDtoResponseShort)
                .build();

        when(userRepository.checkUser(1L)).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));
        ItemDto actual = itemService.addItem(user.getId(), itemDtoRequest);
        assertEquals(actual.getId(), itemDtoRequest.getId());
        verify(itemRepository).save(any());
    }

    @Test
    void updateItem_isValid() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("testUser@email.com")
                .build();

        BookingDtoResponseShort bookingDtoResponseShort = BookingDtoResponseShort.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(0L)
                .name("testItem")
                .description("descriptionForTestItem")
                .available(true)
                .lastBooking(bookingDtoResponseShort)
                .nextBooking(bookingDtoResponseShort)
                .build();

        when(itemRepository.getByIdAndCheck(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));
        when(userRepository.checkUser(any())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));

        ItemDto itemDtoAfter = itemService.updateItem(itemDtoRequest.getId(), itemDtoRequest, user.getId());

        assertEquals(itemDtoAfter.getId(), itemDtoRequest.getId());
        assertEquals(itemDtoAfter.getName(), itemDtoRequest.getName());
        assertEquals(itemDtoAfter.getDescription(), itemDtoRequest.getDescription());
    }

    @Test
    void updateItem_NameNull() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("testUser@email.com")
                .build();

        BookingDtoResponseShort bookingDtoResponseShort = BookingDtoResponseShort.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(0L)
                .description("descriptionForTestItem")
                .available(true)
                .lastBooking(bookingDtoResponseShort)
                .nextBooking(bookingDtoResponseShort)
                .build();

        when(itemRepository.getByIdAndCheck(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));
        when(userRepository.checkUser(any())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));

        ItemDto itemDtoAfter = itemService.updateItem(itemDtoRequest.getId(), itemDtoRequest, user.getId());

        assertEquals(itemDtoAfter.getId(), itemDtoRequest.getId());
        assertEquals(itemDtoAfter.getName(), itemDtoRequest.getName());
        assertEquals(itemDtoAfter.getDescription(), itemDtoRequest.getDescription());
    }

    @Test
    void updateItem_DescriptionNull() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("testUser@email.com")
                .build();

        BookingDtoResponseShort bookingDtoResponseShort = BookingDtoResponseShort.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(0L)
                .name("testItem")
                .available(true)
                .lastBooking(bookingDtoResponseShort)
                .nextBooking(bookingDtoResponseShort)
                .build();

        when(itemRepository.getByIdAndCheck(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));
        when(userRepository.checkUser(any())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));

        ItemDto itemDtoAfter = itemService.updateItem(itemDtoRequest.getId(), itemDtoRequest, user.getId());

        assertEquals(itemDtoAfter.getId(), itemDtoRequest.getId());
        assertEquals(itemDtoAfter.getName(), itemDtoRequest.getName());
        assertEquals(itemDtoAfter.getDescription(), itemDtoRequest.getDescription());
    }

    @Test
    void updateItem_AvailableNull() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("testUser@email.com")
                .build();

        BookingDtoResponseShort bookingDtoResponseShort = BookingDtoResponseShort.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(0L)
                .name("testItem")
                .description("descriptionForTestItem")
                .lastBooking(bookingDtoResponseShort)
                .nextBooking(bookingDtoResponseShort)
                .build();

        when(itemRepository.getByIdAndCheck(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));
        when(userRepository.checkUser(any())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDtoRequest, user));

        ItemDto itemDtoAfter = itemService.updateItem(itemDtoRequest.getId(), itemDtoRequest, user.getId());

        assertEquals(itemDtoAfter.getId(), itemDtoRequest.getId());
        assertEquals(itemDtoAfter.getName(), itemDtoRequest.getName());
        assertEquals(itemDtoAfter.getDescription(), itemDtoRequest.getDescription());
    }

}
