package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    void testBookingDto() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("testItem")
                .description("test description")
                .available(true)
                .build();

        BookingDtoRequest booking = BookingDtoRequest.builder()
                .id(1L)
                .bookerId(888L)
                .itemId(item.getId())
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.of(2023, 11, 15, 10, 11))
                .end(LocalDateTime.of(2023, 12, 15, 10, 11))
                .build();

        JsonContent<BookingDtoRequest> result = json.write(booking);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(booking.getId()));

        assertThat(result)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(Math.toIntExact(booking.getItemId()));

        assertThat(result)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(Math.toIntExact(booking.getBookerId()));

        assertThat(result)
                .extractingJsonPathStringValue("$.status")
                .isEqualTo(booking.getStatus().toString());

        assertThat(result)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(booking.getStart().toString() + ":00");

        assertThat(result)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(booking.getEnd().toString() + ":00");
    }
}
