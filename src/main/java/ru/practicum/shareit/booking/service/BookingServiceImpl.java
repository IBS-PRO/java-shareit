package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
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
    public BookingDtoResponse addBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        User booker = userRepository.checkUser(userId);
        Item item = itemRepository.getByIdAndCheck(bookingDtoRequest.getItemId());
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Пересечение между бронированием и владельцем ");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        bookingDtoRequest.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.toBooking(bookingDtoRequest, booker, item);
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
    public List<BookingDtoResponse> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        userRepository.checkUser(ownerId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> result;
        PageRequest request = PageRequest.of(from, size);
        switch (state) {
            case "ALL":
                result = bookingRepository.getAllBySubject_OwnerIdOrderByStartDateDesc(ownerId, request);
                break;
            case "PAST":
                result = bookingRepository.getAllBySubject_OwnerIdAndEndDateIsBeforeOrderByStartDateDesc(ownerId, currentTime, request);
                break;
            case "CURRENT":
                result = bookingRepository.getAllBySubject_Owner_IdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(ownerId, currentTime, currentTime, request);
                break;
            case "FUTURE":
                result = bookingRepository.getAllBySubject_OwnerIdAndStartDateIsAfterOrderByStartDateDesc(ownerId, currentTime, request);
                break;
            case "WAITING":
                result = bookingRepository.getAllBySubject_OwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.WAITING, request);
                break;
            case "REJECTED":
                result = bookingRepository.getAllBySubject_OwnerIdAndStatusOrderByStartDateDesc(ownerId, BookingStatus.REJECTED, request);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.bookingListToBookingReturnDtoList(result);
    }

    @Override
    public List<BookingDtoResponse> getBookerBookings(Long bookerId, String state, Integer from, Integer size) {
        userRepository.checkUser(bookerId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> result = null;
        PageRequest request = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                result = bookingRepository.getAllByRenterIdOrderByStartDateDesc(bookerId, request);
                break;
            case "PAST":
                result = bookingRepository.getAllByRenterIdAndEndDateIsBeforeOrderByStartDateDesc(bookerId, currentTime, request);
                break;
            case "CURRENT":
                result = bookingRepository.getAllByRenterIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(bookerId, currentTime, currentTime, request);
                break;
            case "FUTURE":
                result = bookingRepository.getAllByRenterIdAndStartDateIsAfterOrderByStartDateDesc(bookerId, currentTime, request);
                break;
            case "WAITING":
                result = bookingRepository.getAllByRenterIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING, request);
                break;
            case "REJECTED":
                result = bookingRepository.getAllByRenterIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED, request);
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
