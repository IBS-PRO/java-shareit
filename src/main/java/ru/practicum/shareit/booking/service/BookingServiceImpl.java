package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse addBooking(BookingDto bookingDto, Long userId) {
        User booker = userRepository.checkUser(userId);
        Item item = itemRepository.getByIdAndCheck(bookingDto.getItemId());
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Пересечение между бронированием и владельцем ");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
        bookingDateCheck(booking);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.getBookingAndCheck(bookingId);
        User user = userRepository.checkUser(userId);
        if (booking.getSubject().getOwner().getId().equals(user.getId()) || booking.getRenter().getId().equals(user.getId())) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else {
            throw new NotFoundException("Пользователь не владелец и не создал бронирование");
        }
    }

    @Override
    public BookingDtoResponse addBookingResolution(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.getBookingAndCheck(bookingId);
        User owner = userRepository.checkUser(ownerId);
        if (approved == null) {
            throw new ValidationException("Неверный статус");
        }
        bookingDateCheck(booking);
        if (!owner.getId().equals(booking.getSubject().getOwner().getId())) {
            throw new NotFoundException("Пользователь не владелец бронирования");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDtoResponse> getOwnerBookings(Long ownerId, String state) {
        userRepository.checkUser(ownerId);
        LocalDateTime currentTime = LocalDateTime.now();
        ArrayList<Booking> result;
        switch (state) {
            case "ALL":
                result = bookingRepository.getAllBySubject_OwnerIdOrderByStartDateDesc(ownerId);
                break;
            case "PAST":
                result = bookingRepository.getAllBySubject_OwnerIdAndEndDateIsBeforeOrderByStartDateDesc(ownerId, currentTime);
                break;
            case "CURRENT":
                result = bookingRepository.getAllBySubject_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(ownerId, currentTime, currentTime);
                break;
            case "FUTURE":
                result = bookingRepository.getAllBySubject_OwnerIdAndStartDateIsAfterOrderByStartDateDesc(ownerId, currentTime);
                break;
            case "WAITING":
                result = bookingRepository.getAllBySubject_OwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.getAllBySubject_OwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.bookingListToBookingReturnDtoList(result);
    }

    @Override
    public List<BookingDtoResponse> getBookerBookings(Long bookerId, String state) {
        userRepository.checkUser(bookerId);
        LocalDateTime currentTime = LocalDateTime.now();
        ArrayList<Booking> result = null;
        switch (state) {
            case "ALL":
                result = bookingRepository.getAllByRenterIdOrderByStartDateDesc(bookerId);
                break;
            case "PAST":
                result = bookingRepository.getAllByRenterIdAndEndDateIsBeforeOrderByStartDateDesc(bookerId, currentTime);
                break;
            case "CURRENT":
                result = bookingRepository.getAllByRenterIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(bookerId, currentTime, currentTime);
                break;
            case "FUTURE":
                result = bookingRepository.getAllByRenterIdAndStartDateIsAfterOrderByStartDateDesc(bookerId, currentTime);
                break;
            case "WAITING":
                result = bookingRepository.getAllByRenterIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.getAllByRenterIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.bookingListToBookingReturnDtoList(result);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    private void bookingDateCheck(Booking booking) {
        if (booking.getStartDate().isAfter(booking.getEndDate())) {
            throw new ValidationException("Ошибка, дата начала не может быть после даты завершения");
        }
        if (booking.getStartDate().isEqual(booking.getEndDate())) {
            throw new ValidationException("Ошибка, дата начала и завершения не может быть одинаковой");
        }
        if (booking.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ошибка, дата начала не может быть в прошлом");
        }
        if (booking.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ошибка, дата завершения не может быть в прошлом");
        }
    }
}
