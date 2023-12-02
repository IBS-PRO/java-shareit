package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse addBooking(BookingDtoRequest bookingDtoRequest, Long userId);

    BookingDtoResponse getBooking(Long bookingId, Long userId);

    BookingDtoResponse addBookingResolution(Long bookingId, Long ownerId, Boolean approved);

    List<BookingDtoResponse> getOwnerBookings(Long ownerId, String state);

    List<BookingDtoResponse> getBookerBookings(Long bookerId, String state);

    void deleteBooking(Long id);

}
