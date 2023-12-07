package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid BookingDtoRequest bookingDtoRequest) {
        log.info("POST '/bookings'. Запрос на бронирование вещи с id {} ", userId);
        BookingDtoResponse response = bookingService.addBooking(bookingDtoRequest, userId);
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
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("GET '/bookings/{bookingId}'. Запрос на получение бронирования с id {} и пользователя с id: {} ",
                bookingId, userId);
        BookingDtoResponse response = bookingService.getBooking(bookingId, userId);
        log.info("GET '/bookings/{bookingId}'. Ответ, пользователь с id: {} и его бронирование {}", userId, response);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) Integer size) {
        log.info("GET '/bookings/owner'. Запрос на получение бронирований пользователя с id {}", ownerId);
        List<BookingDtoResponse> response = bookingService.getOwnerBookings(ownerId, state, from, size);
        log.info("GET '/bookings/owner'. Ответ, пользователь с id {} имеет {}, бронирование/ний", ownerId, response.size());
        return response;
    }

    @GetMapping
    public List<BookingDtoResponse> getBookerBookings(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                      @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(name = "size", defaultValue = "20") @Positive @Max(100) Integer size) {
        log.info("GET '/bookings?state'. Запрос на получение бронирований всех вещей текущего пользователя с id {}", bookerId);
        List<BookingDtoResponse> response = bookingService.getBookerBookings(bookerId, state, from, size);
        log.info("GET '/bookings?state'. Ответ, у пользователя с id {} есть {}, бронирование/ний", bookerId, response.size());
        return response;
    }

}
