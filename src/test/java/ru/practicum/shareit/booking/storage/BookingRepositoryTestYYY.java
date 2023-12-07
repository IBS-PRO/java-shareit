package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTestYYY {
    User createdUser1;
    User createdUser2;
    Item createdItem1;
    Item createdItem2;
    ItemRequest createdRequest1;
    ItemRequest createdRequest2;
    Booking createdBooking1;
    Booking createdBooking2;
    Booking createdBooking3;
    Booking createdBooking4;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@mail.com");
        createdUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@mail.com");
        createdUser2 = userRepository.save(user2);

        Item item1 = new Item();
        item1.setName("testItem");
        item1.setDescription("testItemDescription");
        item1.setAvailable(true);
        item1.setOwner(createdUser1);
        createdItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("testItem2");
        item2.setDescription("testItem2Description");
        item2.setAvailable(true);
        item2.setOwner(createdUser2);
        createdItem2 = itemRepository.save(item2);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("testDescription1");
        request1.setRequestId(user1.getId());
        request1.setCreated(LocalDateTime.now().minusDays(5));
        createdRequest1 = requestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("testDescription1");
        request2.setRequestId(user1.getId());
        request2.setCreated(LocalDateTime.now().minusDays(2));
        createdRequest2 = requestRepository.save(request2);

        LocalDateTime currentTime = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setSubject(createdItem1);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStartDate(currentTime.plusDays(5));
        booking1.setEndDate(currentTime.plusDays(7));
        booking1.setRenter(createdUser1);
        createdBooking1 = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setSubject(createdItem1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStartDate(currentTime.minusDays(7));
        booking2.setEndDate(currentTime.minusDays(5));
        booking2.setRenter(createdUser1);
        createdBooking2 = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setSubject(createdItem2);
        booking3.setStatus(BookingStatus.REJECTED);
        booking3.setStartDate(currentTime.minusDays(10));
        booking3.setEndDate(currentTime.minusDays(8));
        booking3.setRenter(createdUser1);
        createdBooking3 = bookingRepository.save(booking3);

        Booking booking4 = new Booking();
        booking4.setSubject(createdItem1);
        booking4.setStatus(BookingStatus.APPROVED);
        booking4.setStartDate(currentTime.minusDays(2));
        booking4.setEndDate(currentTime.plusDays(3));
        booking4.setRenter(createdUser1);
        createdBooking4 = bookingRepository.save(booking4);
    }

    @Test
    void getByIdAndCheck_IsBookingValid() {
        assertEquals(createdBooking1, bookingRepository.getBookingAndCheck(createdBooking1.getId()));
    }

    @Test
    void getByIdAndCheck_IsBookingInvalid() {
        assertThrows(NotFoundException.class, () -> bookingRepository.getBookingAndCheck(999L));
    }

    @Test
    void getFirstByItemIdAndStatusAndStartDateIsBeforeOrderByStartDateDesc() {
        Booking actual = bookingRepository.getFirstBySubjectIdAndStatusAndStartDateIsBeforeOrderByStartDateDesc(
                createdItem1.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        assertEquals(createdBooking4, actual);
    }

    @DirtiesContext
    @Test
    void getFirstByItemIdAndStatusAndStartDateIsAfterOrderByStartDateAsc() {
        Booking actual = bookingRepository.getFirstBySubjectIdAndStatusAndStartDateIsAfterOrderByStartDateAsc(
                createdItem1.getId(), BookingStatus.WAITING, LocalDateTime.now());
        assertEquals(createdBooking1, actual);
    }

    @Test
    void getAllByEndDateBeforeAndStatusAndItemInOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllByEndDateBeforeAndStatusAndSubjectInOrderByStartDateDesc(
                LocalDateTime.now(), BookingStatus.APPROVED, List.of(createdItem1));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking2));
    }

    @Test
    void getAllByStartDateAfterAndStatusAndItemInOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllByStartDateAfterAndStatusAndSubjectInOrderByStartDateDesc(
                LocalDateTime.now(), BookingStatus.WAITING, List.of(createdItem1));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking1));
    }

    @Test
    void getAllByItem_OwnerIdOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllBySubject_OwnerIdOrderByStartDateDesc(
                createdItem2.getOwner().getId(), PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking3));
    }

    @Test
    void getAllByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository
                .getAllBySubject_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                        createdItem1.getOwner().getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking4));
    }

    @Test
    void getAllByItem_OwnerIdAndEndDateIsBeforeOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository
                .getAllBySubject_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                        createdItem1.getOwner().getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking4));
    }

    @Test
    void getAllByItem_OwnerIdAndStartDateIsAfterOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllBySubject_OwnerIdAndStartDateIsAfterOrderByStartDateDesc(createdItem1.getOwner().getId(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking1));
    }

    @Test
    void getAllByItem_OwnerIdAndStatusOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllBySubject_OwnerIdAndStatusOrderByStartDateDesc(createdItem2.getOwner().getId(), BookingStatus.REJECTED, PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking3));
    }

    @Test
    void getAllByBookerIdOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllByRenterIdOrderByStartDateDesc(createdUser1.getId(), PageRequest.of(0, 10));
        assertEquals(4, bookingList.size());
        assertTrue(bookingList.contains(createdBooking1));
        assertTrue(bookingList.contains(createdBooking2));
        assertTrue(bookingList.contains(createdBooking3));
        assertTrue(bookingList.contains(createdBooking4));
    }

    @Test
    void getAllByBookerIdAndItem_IdAndStatusAndEndDateIsBefore() {
        List<Booking> bookingList = bookingRepository.getAllByRenterIdAndSubject_IdAndStatusAndEndDateIsBefore(createdUser1.getId(), createdItem2.getId(), BookingStatus.REJECTED, LocalDateTime.now());
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking3));
    }

    @Test
    void getAllByBookerIdAndStartDateIsAfterOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllByRenterIdAndStartDateIsAfterOrderByStartDateDesc(
                createdUser1.getId(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking1));
    }

    @Test
    void getAllByBookerIdAndEndDateIsBeforeOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllByRenterIdAndEndDateIsBeforeOrderByStartDateDesc(
                createdUser1.getId(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertEquals(2, bookingList.size());
        assertTrue(bookingList.contains(createdBooking2));
        assertTrue(bookingList.contains(createdBooking3));
    }

    @Test
    void getAllByBookerIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository
                .getAllByRenterIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                        createdUser1.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
        assertTrue(bookingList.contains(createdBooking4));
    }

    @Test
    void getAllByBookerIdAndStatusOrderByStartDateDesc() {
        List<Booking> bookingList = bookingRepository.getAllByRenterIdAndStatusOrderByStartDateDesc(
                createdUser1.getId(), BookingStatus.APPROVED, PageRequest.of(0, 10));
        assertEquals(2, bookingList.size());
        assertTrue(bookingList.contains(createdBooking2));
        assertTrue(bookingList.contains(createdBooking4));
    }
}
