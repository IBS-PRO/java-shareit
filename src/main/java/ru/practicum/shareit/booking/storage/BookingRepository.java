package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    default Booking getBookingAndCheck(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Не найдено бронирование с ид: " + id));
    }

    Booking getFirstBySubjectIdAndStatusAndStartDateIsBeforeOrderByStartDateDesc(Long itemId, BookingStatus bookingStatus, LocalDateTime currentTime);

    Booking getFirstBySubjectIdAndStatusAndStartDateIsAfterOrderByStartDateAsc(Long itemId, BookingStatus bookingStatus, LocalDateTime currentTime);

    List<Booking> getAllByEndDateBeforeAndStatusAndSubjectInOrderByStartDateDesc(LocalDateTime currentTime, BookingStatus bookingStatus, List<Item> items);

    List<Booking> getAllByStartDateAfterAndStatusAndSubjectInOrderByStartDateDesc(LocalDateTime currentTime, BookingStatus bookingStatus, List<Item> items);

    List<Booking> getAllBySubject_OwnerIdOrderByStartDateDesc(Long ownerId);

    List<Booking> getAllBySubject_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(Long ownerId, LocalDateTime currentTimeForStart, LocalDateTime currentTimeForEnd);

    List<Booking> getAllBySubject_OwnerIdAndEndDateIsBeforeOrderByStartDateDesc(long ownerId, LocalDateTime now);

    List<Booking> getAllBySubject_OwnerIdAndStartDateIsAfterOrderByStartDateDesc(Long ownerId, LocalDateTime currentTime);

    List<Booking> getAllBySubject_OwnerIdAndStatusOrderByStartDateDesc(Long ownerId, BookingStatus bookingStatus);

    List<Booking> getAllByRenterIdOrderByStartDateDesc(Long bookerId);

    List<Booking> getAllByRenterIdAndSubject_IdAndStatusAndEndDateIsBefore(Long bookerId, Long itemId, BookingStatus bookingStatus, LocalDateTime currentTime);

    List<Booking> getAllByRenterIdAndStartDateIsAfterOrderByStartDateDesc(Long bookerId, LocalDateTime currentDate);

    List<Booking> getAllByRenterIdAndEndDateIsBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime currentDate);

    List<Booking> getAllByRenterIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(Long bookerId, LocalDateTime currentTimeForStart, LocalDateTime currentTimeForEnd);

    List<Booking> getAllByRenterIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStatus bookingStatus);
}
