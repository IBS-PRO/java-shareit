package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceIntegrationTest {

    BookingDtoRequest bookingWithUnavailableItem;
    BookingDtoRequest bookingFromController;
    BookingDtoRequest bookingFromController2;

    BookingDtoResponse createdBooking;
    BookingDtoResponse createdBooking2;

    UserDto createdOwner;
    UserDto createdBooker2;
    UserDto createdBooker;

    ItemDto createdItem;
    ItemDto createdItem2;

    @Autowired
    BookingService bookingService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @BeforeEach
    void beforeEach() {
        UserDto user = new UserDto();
        user.setName("testNameUser");
        user.setEmail("test@mail.com");
        createdOwner = userService.addUser(user);

        UserDto user2 = new UserDto();
        user2.setName("testNameUser2");
        user2.setEmail("test22@mail.com");
        createdBooker2 = userService.addUser(user2);

        UserDto booker = new UserDto();
        booker.setName("testBooker");
        booker.setEmail("testbooker@mail.com");
        createdBooker = userService.addUser(booker);

        ItemDtoRequest item = new ItemDtoRequest();
        item.setName("testItem");
        item.setDescription("testDescription");
        item.setAvailable(true);
        createdItem = itemService.addItem(createdOwner.getId(), item);

        ItemDtoRequest item2 = new ItemDtoRequest();
        item2.setName("testItem");
        item2.setDescription("testDescription");
        item2.setAvailable(false);
        createdItem2 = itemService.addItem(createdOwner.getId(), item2);

        LocalDateTime currentTime = LocalDateTime.now();

        bookingFromController = new BookingDtoRequest();
        bookingFromController.setItemId(createdItem.getId());
        bookingFromController.setStatus(BookingStatus.WAITING);
        bookingFromController.setStart(currentTime.plusDays(1));
        bookingFromController.setEnd(currentTime.plusDays(2));
        createdBooking = bookingService.addBooking(bookingFromController, createdBooker.getId());

        bookingFromController2 = new BookingDtoRequest();
        bookingFromController2.setItemId(createdItem.getId());
        bookingFromController2.setStatus(BookingStatus.REJECTED);
        bookingFromController2.setStart(currentTime.plusMonths(1));
        bookingFromController2.setEnd(currentTime.plusMonths(2));
        createdBooking2 = bookingService.addBooking(bookingFromController2, createdBooker2.getId());

        bookingWithUnavailableItem = new BookingDtoRequest();
        bookingWithUnavailableItem.setItemId(createdItem2.getId());
        bookingWithUnavailableItem.setStart(currentTime.plusDays(2));
        bookingWithUnavailableItem.setEnd(currentTime.plusDays(3));
    }

    @Test
    void addBooking_itemIsInvalid() {
        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingWithUnavailableItem, createdBooker2.getId()));
    }

    @Test
    void create_BookerIsOwner() {
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingWithUnavailableItem, createdOwner.getId()));
    }

    @Test
    void getById() {
        BookingDtoResponse actual = bookingService.getBooking(createdBooking.getId(), createdOwner.getId());

        assertNotNull(actual);
        assertEquals(createdBooking.getItem().getId(), actual.getItem().getId());
        assertEquals(createdBooking.getStart().withNano(0), actual.getStart().withNano(0));
        assertEquals(createdBooking.getEnd().withNano(0), actual.getEnd().withNano(0));
    }

    @Test
    void getById_UserISNotOwnerOrBooker() {
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(createdBooking.getId(), createdBooker2.getId()));
    }

    @Test
    void getAllByOwner() {
        List<BookingDtoResponse> bookings = bookingService.getOwnerBookings(createdOwner.getId(), "WAITING", 0, 10);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(bookingFromController.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getAllByBooker_future() {
        List<BookingDtoResponse> bookings = bookingService.getBookerBookings(createdBooker.getId(),
                "FUTURE", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), createdBooking.getId());
    }

    @Test
    void getAllByBooker_isUserIncorrect() {
        assertThrows(NotFoundException.class, () ->
                bookingService.getBookerBookings(999L, BookingStatus.WAITING.toString(), 0, 10));
    }

    @Test
    void approvingByOwner_isValidApprovedTrue() {
        BookingDtoResponse actual
                = bookingService.addBookingResolution(createdBooking.getId(), createdOwner.getId(), true);
        assertEquals(actual.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void approvingByOwner_isValidApprovedFalse() {
        BookingDtoResponse actual
                = bookingService.addBookingResolution(createdBooking.getId(), createdOwner.getId(), false);
        assertEquals(actual.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void approvingByOwner_isApprovedInvalid() {
        assertThrows(ValidationException.class,
                () -> bookingService.addBookingResolution(createdBooking.getId(), createdOwner.getId(), null));
    }

    @Test
    void approvingByOwner_userIsNotOwner() {
        assertThrows(NotFoundException.class,
                () -> bookingService.addBookingResolution(createdBooking.getId(), createdBooker2.getId(), true));
    }

    @AfterEach
    void afterEach() {
        bookingService.deleteBooking(createdBooking.getId());
        bookingService.deleteBooking(createdBooking2.getId());
        itemService.deleteItem(createdItem.getId(), createdOwner.getId());
        itemService.deleteItem(createdItem2.getId(), createdOwner.getId());
        userService.deleteUser(createdOwner.getId());
        userService.deleteUser(createdBooker.getId());
        userService.deleteUser(createdBooker2.getId());
    }
}
