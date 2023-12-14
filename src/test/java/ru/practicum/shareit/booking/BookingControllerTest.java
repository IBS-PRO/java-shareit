package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingServiceMock;

    @SneakyThrows
    @Test
    void addBooking() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingDtoRequest bookingDto = new BookingDtoRequest();
        bookingDto.setId(123123L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDtoResponse.Item itemShortDto = new BookingDtoResponse.Item(1L, "itemNameTest");

        BookingDtoResponse bookingResponseDto = new BookingDtoResponse();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        bookingResponseDto.setItem(itemShortDto);
        when(bookingServiceMock.addBooking(bookingDto, 1L)).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @SneakyThrows
    @Test
    void getBookerBookings_StateIsInvalid_ConstraintViolationException() {
        when(bookingServiceMock.getBookerBookings(1L, "someString", 0, 1)).thenThrow(ValidationException.class);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "someString")
                        .param("from", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookerBookings_FromIsInvalid_ConstraintViolationException() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getBookerBookings_SizeIsInvalid_ConstraintViolationException() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_StateIsInvalid_ConstraintViolationException() {
        when(bookingServiceMock.getOwnerBookings(1L, "someString", 0, 1)).thenThrow(ValidationException.class);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "someString")
                        .param("from", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_FromIsInvalid_ConstraintViolationException() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_SizeIsInvalid_ConstraintViolationException() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void addBookingResolution() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);


        BookingDtoResponse.Item itemShortDto = new BookingDtoResponse.Item(1L, "itemNameTest");

        BookingDtoResponse bookingResponseDto = new BookingDtoResponse();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        bookingResponseDto.setItem(itemShortDto);

        when(bookingServiceMock.addBookingResolution(bookingResponseDto.getId(), 1L, true)).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingResponseDto.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @SneakyThrows
    @Test
    void getBooking() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingDtoResponse.Item itemShortDto = new BookingDtoResponse.Item(1L, "itemNameTest");

        BookingDtoResponse bookingResponseDto = new BookingDtoResponse();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        bookingResponseDto.setItem(itemShortDto);
        when(bookingServiceMock.getBooking(bookingResponseDto.getId(), 1L)).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", 1L))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @SneakyThrows
    @Test
    void getBookerBookings_isValid() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingDtoResponse.Item itemShortDto = new BookingDtoResponse.Item(1L, "itemNameTest");

        BookingDtoResponse bookingResponseDto = new BookingDtoResponse();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        bookingResponseDto.setItem(itemShortDto);
        when(bookingServiceMock.getBookerBookings(1L, "ALL", 0, 10)).thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingResponseDto))));
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_isCorrect() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingDtoResponse.Item itemShortDto = new BookingDtoResponse.Item(1L, "itemNameTest");

        BookingDtoResponse bookingResponseDto = new BookingDtoResponse();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        bookingResponseDto.setItem(itemShortDto);
        when(bookingServiceMock.getOwnerBookings(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingResponseDto))));
    }

    @SneakyThrows
    @Test
    void deleteBooking() {
        mockMvc.perform(delete("/bookings/{id}", 1L))
                .andExpect(status().isOk());
        verify(bookingServiceMock).deleteBooking(1L);
    }

}
