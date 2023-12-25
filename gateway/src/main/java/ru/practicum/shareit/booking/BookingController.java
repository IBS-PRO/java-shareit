package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody @Valid BookingDto bookingDtoRequest) {
        log.info("POST '/bookings'. Запрос на бронирование вещи с id {} ", userId);
        ResponseEntity<Object> response = bookingClient.addBooking(bookingDtoRequest, userId);
        log.info("POST '/bookings'. Ответ, новая вещь: {}, успешно добавлена ", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> addBookingResolution(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @PathVariable("bookingId") Long bookingId,
                                                   @RequestParam Boolean approved) {
        log.info("PATCH '/bookings/{bookingId}'. Запрос на подтверждение бронирования владельцем с id {} " +
                "и вещи с id {} ", ownerId, bookingId);
        ResponseEntity<Object> response = bookingClient.addBookingResolution(bookingId, ownerId, approved);
        log.info("PATCH '/bookings/{bookingId}'. Ответ, подтверждение бронирования владельцем с id {} и вещи с id {} ", ownerId, bookingId);
        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("GET '/bookings/{bookingId}'. Запрос на получение бронирования с id {} и пользователя с id: {} ",
                bookingId, userId);
        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);
        log.info("GET '/bookings/{bookingId}'. Ответ, пользователь с id: {} и его бронирование {}", userId, response);
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) Integer size) {
        log.info("GET '/bookings/owner'. Запрос на получение бронирований пользователя с id {}", ownerId);
        ResponseEntity<Object> response = bookingClient.getOwnerBookings(ownerId, state, from, size);
        log.info("GET '/bookings/owner'. Ответ, пользователь с id {}", ownerId);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getBookerBookings(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                      @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(name = "size", defaultValue = "20") @Positive @Max(100) Integer size) {
        log.info("GET '/bookings?state'. Запрос на получение бронирований всех вещей текущего пользователя с id {}", bookerId);
        ResponseEntity<Object> response = bookingClient.getBookerBookings(bookerId, state, from, size);
        log.info("GET '/bookings?state'. Ответ, у пользователя с id {}", bookerId);
        return response;
    }

}
