package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceIntegrationTest {
    UserDto itemOwner;
    UserDto user;
    ItemDtoRequest item;
    UserDto createdOwner;
    UserDto createdUser;
    ItemDto createdItem;
    ItemDtoRequest itemForUpdate;
    CommentDto commentDto;
    Booking booking;

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void beforeEach() {
        itemOwner = new UserDto();
        itemOwner.setName("testNameOwner");
        itemOwner.setEmail("testowner@mail.com");
        createdOwner = userService.addUser(itemOwner);

        item = new ItemDtoRequest();
        item.setName("testItem");
        item.setDescription("testItemDescription");
        item.setAvailable(true);
        createdItem = itemService.addItem(createdOwner.getId(), item);

        itemForUpdate = new ItemDtoRequest();
        item.setName("updatedItemName");
        item.setDescription("updTestItemDescription");
        item.setAvailable(true);

        user = new UserDto();
        user.setName("testNameUser");
        user.setEmail("testEmailUser@mail.com");
        createdUser = userService.addUser(user);

        booking = new Booking();
        booking.setRenter(UserMapper.toUser(createdUser));
        booking.setSubject(ItemMapper.toItem(createdItem, UserMapper.toUser(createdOwner)));
        booking.setStartDate(LocalDateTime.now().minusDays(3));
        booking.setEndDate(LocalDateTime.now().minusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking = bookingRepository.save(booking);

        commentDto = new CommentDto();
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setText("testText");
        commentDto.setItemId(createdItem.getId());
        commentDto.setUserId(createdUser.getId());
        commentDto.setAuthorName(createdUser.getName());
    }

    @Test
    void getById() {
        ItemDto actual = itemService.getItem(createdItem.getId(), createdUser.getId());
        assertNull(actual.getLastBooking());
        assertNull(actual.getNextBooking());
    }

    @Test
    void getById_isOwner() {
        ItemDto actual = itemService.getItem(createdItem.getId(), createdOwner.getId());
        assertEquals(actual.getLastBooking().getId(), booking.getId());
    }

    @Test
    void getAllByOwner() {
        List<ItemDto> list = itemService.getAllOwnerItems(createdOwner.getId());
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void searchAvailableItem_isCorrect_returnList() {
        List<ItemDto> itemDtoList = itemService.searchAvailableItem("test");
        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals(createdItem.getName(), itemDtoList.get(0).getName());
    }

    @Test
    void searchAvailableItem_isIncorrect_returnList() {
        List<ItemDto> itemDtoList = itemService.searchAvailableItem("blabla");
        assertNotNull(itemDtoList);
        assertEquals(0, itemDtoList.size());
    }

    @Test
    void update_isValid() {
        ItemDto actual = itemService.updateItem(createdItem.getId(), itemForUpdate, createdOwner.getId());
        assertEquals(actual.getName(), createdItem.getName());
        assertEquals(actual.getDescription(), createdItem.getDescription());
    }

    @Test
    void createComment_isValid() {
        CommentDto actual = itemService.addComment(createdItem.getId(), commentDto, createdUser.getId());
        assertEquals(actual.getCreated().toString(), commentDto.getCreated().toString());
        assertEquals(actual.getItemId(), createdItem.getId());
        assertEquals(actual.getText(), commentDto.getText());
        assertEquals(actual.getAuthorName(), commentDto.getAuthorName());
    }

    @Test
    void createComment_isUserHasNoBookings() {
        assertThrows(ValidationException.class,
                () -> itemService.addComment(createdItem.getId(), commentDto, createdOwner.getId()));
    }

    @AfterEach
    void afterEach() {
        bookingService.deleteBooking(booking.getId());
        userService.deleteUser(createdOwner.getId());
        userService.deleteUser(createdUser.getId());
    }
}
