package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoResponse {
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private BookingStatus status;


    @Data
    @Builder(toBuilder = true)
    @AllArgsConstructor
    public static class Booker {
        private long id;
        private String name;
    }

    @Data
    @Builder(toBuilder = true)
    @AllArgsConstructor
    public static class Item {
        private long id;
        private String name;
    }
}
