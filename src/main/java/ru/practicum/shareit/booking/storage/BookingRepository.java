package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    default Booking getBookingAndCheck(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Не найдено бронирование с ид: " + id));
    }

    Booking getFirstBySubjectIdAndStatusAndStartDateIsBeforeOrderByStartDateDesc(Long itemId, BookingStatus bookingStatus, LocalDateTime currentTime);

    Booking getFirstBySubjectIdAndStatusAndStartDateIsAfterOrderByStartDateAsc(Long itemId, BookingStatus bookingStatus, LocalDateTime currentTime);

    ArrayList<Booking> getAllByEndDateBeforeAndStatusAndSubjectInOrderByStartDateDesc(LocalDateTime currentTime, BookingStatus bookingStatus, List<Item> items);

    ArrayList<Booking> getAllByStartDateAfterAndStatusAndSubjectInOrderByStartDateDesc(LocalDateTime currentTime, BookingStatus bookingStatus, List<Item> items);

    ArrayList<Booking> getAllBySubject_OwnerIdOrderByStartDateDesc(Long ownerId);

    ArrayList<Booking> getAllBySubject_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(Long ownerId, LocalDateTime currentTimeForStart, LocalDateTime currentTimeForEnd);

    ArrayList<Booking> getAllBySubject_OwnerIdAndEndDateIsBeforeOrderByStartDateDesc(long ownerId, LocalDateTime now);

    ArrayList<Booking> getAllBySubject_OwnerIdAndStartDateIsAfterOrderByStartDateDesc(Long ownerId, LocalDateTime currentTime);

    ArrayList<Booking> getAllBySubject_OwnerIdAndStatusOrderByStartDateDesc(Long ownerId, BookingStatus bookingStatus);

    ArrayList<Booking> getAllByRenterIdOrderByStartDateDesc(Long bookerId);

    ArrayList<Booking> getAllByRenterIdAndSubject_IdAndStatusAndEndDateIsBefore(Long bookerId, Long itemId, BookingStatus bookingStatus, LocalDateTime currentTime);

    ArrayList<Booking> getAllByRenterIdAndStartDateIsAfterOrderByStartDateDesc(Long bookerId, LocalDateTime currentDate);

    ArrayList<Booking> getAllByRenterIdAndEndDateIsBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime currentDate);

    ArrayList<Booking> getAllByRenterIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(Long bookerId, LocalDateTime currentTimeForStart, LocalDateTime currentTimeForEnd);

    ArrayList<Booking> getAllByRenterIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStatus bookingStatus);
}
