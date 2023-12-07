package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void getById() {
    }

    @Test
    void create_BookerIsOwner() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("testBooker");
        booker.setEmail("testbooker@mail.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("testDescription");
        item.setAvailable(true);
        item.setOwner(booker);

        BookingDtoRequest booking = new BookingDtoRequest();
        booking.setId(1L);
        booking.setBookerId(booker.getId());
        booking.setItemId(item.getId());
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        when(itemRepository.getByIdAndCheck(any())).thenReturn(item);
        when(userRepository.checkUser(any())).thenReturn(booker);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(booking, booker.getId()));
        verify(itemRepository).getByIdAndCheck(any());
        verify(userRepository).checkUser(any());
    }

    @Test
    void create_ItemIsUnavailable() {
        User ownerOfItem = new User();
        ownerOfItem.setId(2L);
        ownerOfItem.setName("testNameUser2");
        ownerOfItem.setEmail("test22@mail.com");

        User booker = new User();
        booker.setId(1L);
        booker.setName("testBooker");
        booker.setEmail("testbooker@mail.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("testDescription");
        item.setAvailable(false);
        item.setOwner(ownerOfItem);

        BookingDtoRequest booking = new BookingDtoRequest();
        booking.setId(1L);
        booking.setBookerId(booker.getId());
        booking.setItemId(item.getId());
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        when(itemRepository.getByIdAndCheck(any())).thenReturn(item);
        when(userRepository.checkUser(any())).thenReturn(booker);


        assertThrows(ValidationException.class, () -> bookingService.addBooking(booking, booker.getId()));
        verify(itemRepository).getByIdAndCheck(any());
        verify(userRepository).checkUser(any());
    }


    @Test
    void approvingByOwner_isAlreadyApproved() {
        User ownerOfItem = new User();
        ownerOfItem.setId(2L);
        ownerOfItem.setName("testNameUser2");
        ownerOfItem.setEmail("test22@mail.com");

        User booker = new User();
        booker.setId(1L);
        booker.setName("testBooker");
        booker.setEmail("testbooker@mail.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("testDescription");
        item.setAvailable(true);
        item.setOwner(ownerOfItem);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setRenter(booker);
        booking.setSubject(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartDate(LocalDateTime.now().plusHours(1));
        booking.setEndDate(LocalDateTime.now().plusDays(1));

        when(bookingRepository.getBookingAndCheck(1L)).thenReturn(booking);
        when(userRepository.checkUser(2L)).thenReturn(ownerOfItem);
        assertThrows(IllegalArgumentException.class, () -> bookingService.addBookingResolution(1L, 2L, true));
    }

    @Test
    void getAllByBooker_UNSUPPORTED_STATUS() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("testBooker");
        booker.setEmail("testbooker@mail.com");

        assertThrows(ValidationException.class, () -> bookingService.getBookerBookings(booker.getId(), "blabla", 0, 10));
    }
}