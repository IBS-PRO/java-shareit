package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid BookingDto bookingDto) {
        log.info("POST '/bookings'. Запрос на бронирование вещи с id {} ", userId);
        BookingDtoResponse response = bookingService.addBooking(bookingDto, userId);
        log.info("POST '/bookings'. Ответ, новая вещь: {}, успешно добавлена ", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse addBookingResolution(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @PathVariable("bookingId") Long bookingId,
                                                   @RequestParam Boolean approved) {
        log.info("PATCH '/bookings/{bookingId}'. Запрос на подтверждение бронирования владельцем с id {} " +
                "и вещи с id {} ", ownerId, bookingId);
        BookingDtoResponse response = bookingService.addBookingResolution(bookingId, ownerId, approved);
        log.info("PATCH '/bookings/{bookingId}'. Ответ, подтверждение бронирования владельцем с id {} и вещи с id {}," +
                " резолюция - {} ", ownerId, bookingId, response.getStatus());
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        log.info("GET '/bookings/{bookingId}'. Запрос на получение бронирования с id {} и пользователя с id: {} ",
                bookingId, userId);
        BookingDtoResponse response = bookingService.getBooking(bookingId, userId);
        log.info("GET '/bookings/{bookingId}'. Ответ, пользователь с id: {} и его бронирование {}", userId, response);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET '/bookings/owner'. Запрос на получение бронирований пользователя с id {}", ownerId);
        List<BookingDtoResponse> response = bookingService.getOwnerBookings(ownerId, state);
        log.info("GET '/bookings/owner'. Ответ, пользователь с id {} имеет {}, бронирование/ний", ownerId, response.size());
        return response;
    }

    @GetMapping
    public List<BookingDtoResponse> getBookerBookings(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET '/bookings?state'. Запрос на получение бронирований всех вещей текущего пользователя с id {}", bookerId);
        List<BookingDtoResponse> response = bookingService.getBookerBookings(bookerId, state);
        log.info("GET '/bookings?state'. Ответ, у пользователя с id {} есть {}, бронирование/ний", bookerId, response.size());
        return response;
    }

}
