package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        if (bookingDtoRequest == null || booker == null || item == null) {
            throw new IllegalArgumentException("Аргументы toBooking не могут быть null");
        }
        return Booking.builder()
                .id(bookingDtoRequest.getId())
                .startDate(bookingDtoRequest.getStart())
                .endDate(bookingDtoRequest.getEnd())
                .subject(item)
                .renter(booker)
                .status(bookingDtoRequest.getStatus())
                .build();
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("booking не могут быть null");
        }
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(new BookingDtoResponse.Item.ItemBuilder()
                        .id(booking.getSubject().getId())
                        .name(booking.getSubject().getName())
                        .build())
                .booker(new BookingDtoResponse.Booker.BookerBuilder()
                        .id(booking.getRenter().getId())
                        .name(booking.getRenter().getName())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoResponseShort toBookingShortDto(Booking booking) {
        BookingDtoResponseShort bookingResponseShortDto = new BookingDtoResponseShort();
        if (booking != null) {
            bookingResponseShortDto.setId(booking.getId());
            bookingResponseShortDto.setBookerId(booking.getRenter().getId());
        } else {
            bookingResponseShortDto = null;
        }
        return bookingResponseShortDto;
    }

    public static List<BookingDtoResponse> bookingListToBookingReturnDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}
