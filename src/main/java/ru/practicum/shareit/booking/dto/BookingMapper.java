package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoTwoFields;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoOneField;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
        if (bookingDto == null || booker == null || item == null) {
            throw new IllegalArgumentException("Аргументы toBooking не могут быть null");
        }
        return Booking.builder()
                .id(bookingDto.getId())
                .startDate(bookingDto.getStart())
                .endDate(bookingDto.getEnd())
                .subject(item)
                .renter(booker)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        //
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(new ItemDtoTwoFields(booking.getSubject().getId(), booking.getSubject().getName()))
                .booker(new UserDtoOneField(booking.getRenter().getId()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoResponseTwoField toBookingShortDto(Booking booking) {
        BookingDtoResponseTwoField bookingResponseShortDto = new BookingDtoResponseTwoField();
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
