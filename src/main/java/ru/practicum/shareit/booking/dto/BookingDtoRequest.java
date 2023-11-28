package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDtoRequest {
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    @Enumerated(EnumType.ORDINAL)
    private BookingStatus status;
}
